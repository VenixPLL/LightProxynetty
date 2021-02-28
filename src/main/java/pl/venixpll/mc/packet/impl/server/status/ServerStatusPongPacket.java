package pl.venixpll.mc.packet.impl.server.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ServerStatusPongPacket extends Packet {

    private long time;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeLong(this.time);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.time = in.readLong();
    }
}
