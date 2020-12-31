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
public class ClientStatusPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x16, 47));
        this.getProtocolList().add(new Protocol(0x03, 340));
    }

    private int actionId;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarIntToBuffer(actionId);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.actionId = in.readVarIntFromBuffer();
    }
}
