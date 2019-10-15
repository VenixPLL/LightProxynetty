package pl.venixpll.mc.objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.connection.ServerConnector;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.netty.NettyCompressionCodec;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.NetHandlerLoginServer;
import pl.venixpll.mc.packet.impl.client.NetHandlerStatusServer;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSetCompressionPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerChatPacket;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {

    private Channel channel;
    private EnumConnectionState connectionState;
    private INetHandler packetHandler;
    private String username;
    private int ping;
    private ServerConnector connector;

    private List<Bot> bots = new ArrayList<>();

    public void sendChatMessage(final String message,final Object... args){
        sendPacket(new ServerChatPacket("&fLight&6Proxy &8» &6" + String.format(message,args)));
    }

    public void packetReceived(final Packet packet){
        if(packet instanceof HandshakePacket){
            final HandshakePacket handshake = (HandshakePacket) packet;
            switch(handshake.getNextState()){
                case 1:
                    setConnectionState(EnumConnectionState.STATUS);
                    packetHandler = new NetHandlerStatusServer(this);
                    break;
                case 2:
                    setConnectionState(EnumConnectionState.LOGIN);
                    packetHandler = new NetHandlerLoginServer(this);
                    break;
            }
            if(connectionState == EnumConnectionState.HANDSHAKE){
                channel.close();
                return;
            }
        }else{
            packetHandler.handlePacket(packet);
        }
    }

    public void disconnected(){
        if(this.getConnector() != null && this.getConnector().isConnected()){
            this.getConnector().close();
        }
        LightProxy.getServer().playerList.remove(this);
        if(packetHandler != null)
            packetHandler.disconnected();
    }

    public void setConnectionState(final EnumConnectionState state){
        ((NettyPacketCodec)channel.pipeline().get("packetCodec")).setConnectionState(state);
        connectionState = state;
    }

    public void setCompressionThreshold(final int threshold){
        if(connectionState == EnumConnectionState.LOGIN) {
            sendPacket(new ServerLoginSetCompressionPacket(threshold));
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new NettyCompressionCodec(threshold));
            } else {
                ((NettyCompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        }else{
            throw new UnsupportedOperationException();
        }
    }

    public void sendPacket(final Packet packet){
        this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }



}
