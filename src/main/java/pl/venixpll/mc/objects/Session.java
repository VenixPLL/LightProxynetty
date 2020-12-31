package pl.venixpll.mc.objects;

import io.netty.channel.Channel;
import lombok.Data;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.netty.NettyCompressionCodec;
import pl.venixpll.mc.netty.NettyPacketCodec;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;

@Data
public class Session {
    private final Channel channel;
    private INetHandler packetHandler;
    private String username;

    public void sendPacket(Packet p) {
        if(channel.isOpen()) { channel.writeAndFlush(p); }
    }

    public void setConnectionState(EnumConnectionState state) {
        ((NettyPacketCodec) channel.pipeline().get("packetCodec")).setConnectionState(state);
    }

    public EnumConnectionState getConnectionState() {
        return ((NettyPacketCodec) channel.pipeline().get("packetCodec")).getConnectionState();
    }

    public void setCompressionThreshold(final int threshold) {
        if(getConnectionState() == EnumConnectionState.LOGIN) {
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new NettyCompressionCodec(threshold));
            } else {
                ((NettyCompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void setProtocolID(int protocol) {
        ((NettyPacketCodec) channel.pipeline().get("packetCodec")).setProtocol(protocol);
    }

    public int getProtocolID() {
        return ((NettyPacketCodec) channel.pipeline().get("packetCodec")).getProtocol();
    }
}