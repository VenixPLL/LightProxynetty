package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientEntityActionPacket extends Packet {

    private int entityId, actionId, actionParameter;

    {
        this.setPacketID(0x0B);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(entityId);
        out.writeVarIntToBuffer(actionId);
        out.writeVarIntToBuffer(actionParameter);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarIntFromBuffer();
        this.actionId = in.readVarIntFromBuffer();
        this.actionParameter = in.readVarIntFromBuffer();
    }
}
