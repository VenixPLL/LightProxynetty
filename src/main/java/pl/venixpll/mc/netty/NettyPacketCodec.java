/*
 * LightProxy
 * Copyright (C) 2021.  VenixPLL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package pl.venixpll.mc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.impl.CustomPacket;
import pl.venixpll.mc.packet.registry.PacketRegistry;

import java.util.List;

@AllArgsConstructor
@Data
public class NettyPacketCodec extends ByteToMessageCodec<Packet> {

    private EnumConnectionState connectionState;
    private EnumPacketDirection packetDirection;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        final PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
        if (packet.isCustom()) {
            packetbuffer.writeVarIntToBuffer(packet.getPacketID());
            packetbuffer.writeBytes(packet.getCustomData());
        } else {
            packetbuffer.writeVarIntToBuffer(packet.getPacketID());
            packet.write(packetbuffer);
        }
        packet = null;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (!byteBuf.isReadable()) return;
        try {
            final PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

            final int packetId = packetBuffer.readVarIntFromBuffer();

            Packet packet = PacketRegistry.getPacket(connectionState, packetDirection, packetId);

            if (packet == null) {
                packet = new CustomPacket();
                final byte[] data = new byte[packetBuffer.readableBytes()];
                packetBuffer.readBytes(data);
                packet.setCustom(packetId, data);
            } else {
                packet.read(packetBuffer);
            }

            if (packetBuffer.isReadable()) {
                throw new DecoderException(String.format("Packet (%s) was larger than i expected found %s bytes extra", packet.getClass().getSimpleName(), packetBuffer.readableBytes()));
            }
            list.add(packet);
            byteBuf.clear();
            packet = null;
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
