package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.game.Difficulty;
import pl.venixpll.mc.data.game.Dimension;
import pl.venixpll.mc.data.game.Gamemode;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerJoinGamePacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x01, 47));
        this.getProtocolList().add(new Protocol(0x23, 110));
        this.getProtocolList().add(new Protocol(0x23, 340));
    }

    private int entityId;
    private Gamemode gamemode;
    private Dimension dimension;
    private Difficulty difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reduced_debug;


    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeInt(this.entityId);
        out.writeByte(this.gamemode.getId());//u
        if(protocol >= 110) {
            out.writeInt(this.dimension.getId());
        } else {
            out.writeByte(this.dimension.getId());
        }
        out.writeByte(this.difficulty.getId());//u
        out.writeByte(this.maxPlayers);//u
        out.writeString(this.levelType);
        out.writeBoolean(this.reduced_debug);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.entityId = in.readInt();
        this.gamemode = Gamemode.getById(in.readUnsignedByte());
        if(protocol >= 110) {
            this.dimension = Dimension.getById(in.readInt());
        } else {
            this.dimension = Dimension.getById(in.readByte());
        }
        this.difficulty = Difficulty.getById(in.readUnsignedByte());
        this.maxPlayers = in.readUnsignedByte();
        this.levelType = in.readStringFromBuffer(16);

        if (this.levelType == null)
        {
            this.levelType = "default";
        }

        this.reduced_debug = in.readBoolean();
    }
}
