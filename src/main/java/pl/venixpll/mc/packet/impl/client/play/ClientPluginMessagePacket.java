package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPluginMessagePacket extends Packet {

    private String channel;
    private byte[] data;

    {
        this.setPacketID(0x17);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(channel);
        out.writeBytes(data);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.channel = in.readStringFromBuffer(16);
        data = new byte[in.readableBytes()];
        in.readBytes(data);
    }
}
