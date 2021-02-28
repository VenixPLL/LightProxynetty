package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatusPacket extends Packet {

    private int actionId;

    {
        this.setPacketID(0x16);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(actionId);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.actionId = in.readVarIntFromBuffer();
    }
}
