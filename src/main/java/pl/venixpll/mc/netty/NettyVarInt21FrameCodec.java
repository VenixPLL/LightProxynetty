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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import pl.venixpll.mc.packet.PacketBuffer;

import java.util.List;

public class NettyVarInt21FrameCodec extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        final int size = byteBuf.readableBytes();
        final int j = PacketBuffer.getVarIntSize(size);
        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + size + " into 3");
        }

        final PacketBuffer packetbuffer = new PacketBuffer(byteBuf2);
        packetbuffer.ensureWritable(j + size);
        packetbuffer.writeVarIntToBuffer(size);
        packetbuffer.writeBytes(byteBuf, byteBuf.readerIndex(), size);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        final byte[] bytes = new byte[3];

        for (int i = 0; i < bytes.length; ++i) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bytes[i] = byteBuf.readByte();

            if (bytes[i] >= 0) {
                final PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
                try {
                    final int j = packetbuffer.readVarIntFromBuffer();
                    if (byteBuf.readableBytes() >= j) {
                        list.add(byteBuf.readBytes(j));
                        return;
                    }

                    byteBuf.resetReaderIndex();
                } finally {
                    packetbuffer.release();
                }
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

