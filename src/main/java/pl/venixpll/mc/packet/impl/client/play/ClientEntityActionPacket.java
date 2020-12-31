package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientEntityActionPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x0B, 47));
        this.getProtocolList().add(new Protocol(0x14, 110));
        this.getProtocolList().add(new Protocol(0x15, 340));
    }


    private int entityId,actionId,actionParameter;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarIntToBuffer(entityId);
        out.writeVarIntToBuffer(actionId);
        out.writeVarIntToBuffer(actionParameter);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.entityId = in.readVarIntFromBuffer();
        this.actionId = in.readVarIntFromBuffer();
        this.actionParameter = in.readVarIntFromBuffer();
    }
}
