package pl.venixpll.mc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import pl.venixpll.mc.packet.PacketBuffer;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class NettyCompressionCodec extends ByteToMessageCodec<ByteBuf> {

    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private final Inflater inflater;
    private int threshold;

    public NettyCompressionCodec(int thresholdIn) {
        this.threshold = thresholdIn;
        this.deflater = new Deflater();
        this.inflater = new Inflater();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
        int readable = in.readableBytes();
        PacketBuffer output = new PacketBuffer(out);
        if (readable < this.threshold) {
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
                if (size < this.threshold) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + this.threshold + ".");
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

    public void setCompressionThreshold(int thresholdIn) {
        threshold = thresholdIn;
    }
}
