package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPluginMessagePacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x17, 47));
        this.getProtocolList().add(new Protocol(0x09, 110));
        this.getProtocolList().add(new Protocol(0x09, 340));
    }

    private String channel;
    private byte[] data;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(channel);
        out.writeBytes(data);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.channel = in.readStringFromBuffer(16);
        data = new byte[in.readableBytes()];
        in.readBytes(data);
    }
}
