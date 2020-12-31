package pl.venixpll.mc.objects;

import com.darkmagician6.eventapi.EventManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import pl.venixpll.LightProxy;
import pl.venixpll.events.PacketReceivedEvent;
import pl.venixpll.mc.connection.ServerConnector;
import pl.venixpll.mc.data.chat.MessagePosition;
import pl.venixpll.mc.data.game.TitleAction;
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
import pl.venixpll.mc.packet.impl.server.play.ServerTitlePacket;

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
    private String lastPacket;
    private boolean debugInfo;
    private int packetID;

    private boolean mother;

    private List<Bot> bots = new ArrayList<>();

    public void resetTitle(){
        sendPacket(new ServerTitlePacket(TitleAction.RESET));
    }

    public void sendTitle(final String header,final String footer){
        this.sendTitle(header,footer,10,10,10);
    }

    public void sendTitle(final String header,final String footer,final int fadeIn,final int stay,final int fadeOut){
        if(header != null) sendPacket(new ServerTitlePacket(TitleAction.TITLE,header));
        if(footer != null) sendPacket(new ServerTitlePacket(TitleAction.SUBTITLE,footer));
        sendPacket(new ServerTitlePacket(TitleAction.TIMES,fadeIn,stay,fadeOut));
    }

    public void sendHotbar(final String message, final Object... obj){
        sendPacket(new ServerChatPacket(String.format(message,obj), MessagePosition.HOTBAR));
    }

    public final void tick(){
        if(this.getConnector() != null && this.getConnector().isConnected()){
            final int packetTime = (int) (System.currentTimeMillis() - this.getConnector().getLastPacketTime());
            if(packetTime > 2000){
                sendTitle("&cServer is not responding!",String.format("&6%sms",packetTime),0,10,0);
            }else if(debugInfo){
                sendHotbar("&aLast packet received&8: &6%s &7(&cID: &e%s&7)",lastPacket,packetID);
            }
        }
    }

    public void sendChatMessage(final String message,final Object... args){
        sendPacket(new ServerChatPacket("&fLight&6Proxy &8» &6" + String.format(message,args)));
    }

    public void sendChatMessageNoPrefix(final String message, final Object... args) {
        sendPacket(new ServerChatPacket(String.format(message,args)));
    }

    public void packetReceived(final Packet packet){
        final PacketReceivedEvent event = new PacketReceivedEvent(packet,this);
        EventManager.call(event);
        if(!event.isCancelled()) {
            if (packet instanceof HandshakePacket) {
                final HandshakePacket handshake = (HandshakePacket) packet;
                switch (handshake.getNextState()) {
                    case 1:
                        setConnectionState(EnumConnectionState.STATUS);
                        packetHandler = new NetHandlerStatusServer(this);
                        break;
                    case 2:
                        setConnectionState(EnumConnectionState.LOGIN);
                        packetHandler = new NetHandlerLoginServer(this);
                        break;
                }
                if (connectionState == EnumConnectionState.HANDSHAKE) {
                    channel.close();
                    return;
                }
            } else {
                packetHandler.handlePacket(packet);
            }
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
