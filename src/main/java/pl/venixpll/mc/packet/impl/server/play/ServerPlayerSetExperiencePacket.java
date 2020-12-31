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
public class ServerPlayerSetExperiencePacket extends Packet {
    {
        this.getProtocolList().add(new Protocol(0x1F, 47));
        this.getProtocolList().add(new Protocol(0x3D, 110));
        this.getProtocolList().add(new Protocol(0x40, 340));
    }

    private float experience;
    private int level;
    private int totalExperience;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeFloat(experience);
        out.writeVarIntToBuffer(level);
        out.writeVarIntToBuffer(totalExperience);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.experience = in.readFloat();
        this.level = in.readVarIntFromBuffer();
        this.totalExperience = in.readVarIntFromBuffer();
    }
}
