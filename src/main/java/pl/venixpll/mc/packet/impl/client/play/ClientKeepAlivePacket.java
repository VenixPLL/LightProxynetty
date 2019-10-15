package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ClientKeepAlivePacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    private int time;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.time);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.time = in.readVarIntFromBuffer();
    }
}
