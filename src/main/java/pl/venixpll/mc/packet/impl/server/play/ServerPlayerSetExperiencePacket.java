package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerPlayerSetExperiencePacket extends Packet {
    {
        this.setPacketID(0x1F);
    }

    private float experience;
    private int level;
    private int totalExperience;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeFloat(experience);
        out.writeVarIntToBuffer(level);
        out.writeVarIntToBuffer(totalExperience);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.experience = in.readFloat();
        this.level = in.readVarIntFromBuffer();
        this.totalExperience = in.readVarIntFromBuffer();
    }
}
