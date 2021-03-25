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
import io.netty.handler.codec.DecoderException;
import lombok.Setter;
import pl.venixpll.mc.packet.PacketBuffer;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Setter
public class NettyCompressionCodec extends ByteToMessageCodec<ByteBuf> {

    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private final Inflater inflater;
    private int compressionThreshold;

    public NettyCompressionCodec(int thresholdIn) {
        this.compressionThreshold = thresholdIn;
        this.deflater = new Deflater();
        this.inflater = new Inflater();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
        int readable = in.readableBytes();
        PacketBuffer output = new PacketBuffer(out);
        if (readable < this.compressionThreshold) {
            output.writeVarIntToBuffer(0);
            out.writeBytes(in);
        } else {
            byte[] bytes = new byte[readable];
            in.readBytes(bytes);
            output.writeVarIntToBuffer(bytes.length);
            this.deflater.setInput(bytes, 0, readable);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                int length = this.deflater.deflate(this.buffer);
                output.writeBytes(buffer, length);
            }

            this.deflater.reset();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() != 0) {
            PacketBuffer in = new PacketBuffer(buf);
            int size = in.readVarIntFromBuffer();
            if (size == 0) {
                out.add(buf.readBytes(buf.readableBytes()));
            } else {
                if (size < this.compressionThreshold) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + this.compressionThreshold + ".");
                }

                if (size > 2097152) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is larger than protocol maximum of " + 2097152 + ".");
                }

                byte[] bytes = new byte[buf.readableBytes()];
                in.readBytes(bytes);
                this.inflater.setInput(bytes);
                byte[] inflated = new byte[size];
                this.inflater.inflate(inflated);
                out.add(Unpooled.wrappedBuffer(inflated));
                this.inflater.reset();
            }
        }
    }
}
