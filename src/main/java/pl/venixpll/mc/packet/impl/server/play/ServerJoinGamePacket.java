package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.game.Difficulty;
import pl.venixpll.mc.data.game.Dimension;
import pl.venixpll.mc.data.game.Gamemode;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerJoinGamePacket extends Packet {

    private int entityId;
    private Gamemode gamemode;
    private Dimension dimension;
    private Difficulty difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reduced_debug;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeInt(this.entityId);
        out.writeByte(this.gamemode.getId());//u
        out.writeByte(this.dimension.getId());
        out.writeByte(this.difficulty.getId());//u
        out.writeByte(this.maxPlayers);//u
        out.writeString(this.levelType);
        out.writeBoolean(this.reduced_debug);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readInt();
        this.gamemode = Gamemode.getById(in.readUnsignedByte());
        this.dimension = Dimension.getById(in.readByte());
        this.difficulty = Difficulty.getById(in.readUnsignedByte());
        this.maxPlayers = in.readUnsignedByte();
        this.levelType = in.readStringFromBuffer(32767);
        this.reduced_debug = in.readBoolean();
    }
}
