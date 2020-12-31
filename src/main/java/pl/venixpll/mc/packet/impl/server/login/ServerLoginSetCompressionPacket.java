package pl.venixpll.mc.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServerLoginSetCompressionPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x03, 47));
        this.getProtocolList().add(new Protocol(0x03, 110));
        this.getProtocolList().add(new Protocol(0x03, 340));
    }

    private int threshold;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarIntToBuffer(this.threshold);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.threshold = in.readVarIntFromBuffer();
    }
}
