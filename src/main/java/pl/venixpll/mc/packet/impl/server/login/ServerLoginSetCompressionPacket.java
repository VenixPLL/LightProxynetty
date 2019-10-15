package pl.venixpll.mc.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServerLoginSetCompressionPacket extends Packet {

    {
        this.setPacketID(0x03);
    }

    private int threshold;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.threshold);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.threshold = in.readVarIntFromBuffer();
    }
}
