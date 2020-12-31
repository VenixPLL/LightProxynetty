package pl.venixpll.mc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.Data;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;
import pl.venixpll.mc.packet.impl.CustomPacket;
import pl.venixpll.mc.packet.registry.PacketRegistry;

import java.util.Arrays;
import java.util.List;

@Data
public class NettyPacketCodec extends ByteToMessageCodec<Packet> {

    private EnumConnectionState connectionState;
    private EnumPacketDirection packetDirection;
    private int protocol;

    public NettyPacketCodec(EnumConnectionState connectionState, EnumPacketDirection packetDirection) {
        this.connectionState = connectionState;
        this.packetDirection = packetDirection;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        final PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
        packetbuffer.writeVarIntToBuffer(getPacketIDByProtocol(packet, protocol));
        packet.write(packetbuffer, protocol);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        if (!byteBuf.isReadable()) return;
        try {
            final PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

            final int packetID = packetBuffer.readVarIntFromBuffer();

            Packet packet = PacketRegistry.getPacket(connectionState,packetDirection,packetID,protocol);

            final boolean debug = true;
            if(debug) {
                final ByteBuf bufDUPLICATE = byteBuf.duplicate();
                final byte[] data = new byte[bufDUPLICATE.readableBytes()];
                bufDUPLICATE.readBytes(data);
                if(data.length > 1) {
                    System.err.println("[" + channelHandlerContext.channel().remoteAddress() + "] Dane pakietu " + packet.getClass().getSimpleName() + "(" + packetID + "): " + Arrays.toString(data));
                }
                bufDUPLICATE.clear();
            }

            packet.read(packetBuffer, protocol);
            if (packetBuffer.isReadable()) {
                throw new DecoderException(String.format("Packet (%s) was larger than i expected found %s bytes extra",packet.getClass().getSimpleName(),packetBuffer.readableBytes()));
            }
            list.add(packet);
            byteBuf.clear();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getPacketIDByProtocol(Packet packet, int protocol) {
        if(packet instanceof CustomPacket) { return ((CustomPacket)packet).getCustomPacketID(); }
        for(Protocol p : packet.getProtocolList()) {
            if(p.getProtocol() == protocol) {
                return p.getId();
            }
        }
        return packet.getProtocolList().get(0).getId();
    }
}
