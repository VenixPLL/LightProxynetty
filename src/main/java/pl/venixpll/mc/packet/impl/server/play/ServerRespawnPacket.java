package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.game.Difficulty;
import pl.venixpll.mc.data.game.Dimension;
import pl.venixpll.mc.data.game.Gamemode;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerRespawnPacket extends Packet {

    private Dimension dimension;
    private Difficulty difficulty;
    private Gamemode gamemode;
    private String level_type;

    {
        this.setPacketID(0x07);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.dimension.getId());
        out.writeByte(this.difficulty.getId());
        out.writeByte(this.gamemode.getId());
        out.writeString(this.level_type);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.dimension = Dimension.getById(in.readInt());
        this.difficulty = Difficulty.getById(in.readUnsignedByte());
        this.gamemode = Gamemode.getById(in.readUnsignedByte());
        this.level_type = in.readStringFromBuffer(24);
    }
}
