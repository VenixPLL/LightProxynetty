package pl.venixpll.mc.packet.impl.client.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ClientStatusPingPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x01, 47));
        this.getProtocolList().add(new Protocol(0x01, 110));
        this.getProtocolList().add(new Protocol(0x01, 340));
    }

    private long time;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeLong(this.time);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.time = in.readLong();
    }
}
