package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerTimeUpdatePacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x03, 47));
        this.getProtocolList().add(new Protocol(0x44, 110));
        this.getProtocolList().add(new Protocol(0x47, 340));
    }

    private long worldAge;
    private long dayTime;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeLong(this.worldAge);
        out.writeLong(this.dayTime);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.worldAge = in.readLong();
        this.dayTime = in.readLong();
    }
}
