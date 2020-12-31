package pl.venixpll.mc.packet.impl.server.play;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;


@NoArgsConstructor
@Data
public class ServerPlayerPositionRotationPacket extends Packet {

    public ServerPlayerPositionRotationPacket(Position pos, float yaw, float pitch) {
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public ServerPlayerPositionRotationPacket(Position pos, float yaw, float pitch, int teleport) {
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.teleport = teleport;
    }

    private Position pos;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private int teleport;

    {
        this.getProtocolList().add(new Protocol(0x08, 47));
        this.getProtocolList().add(new Protocol(0x2E, 110));
        this.getProtocolList().add(new Protocol(0x2F, 340));
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeDouble(this.pos.getX());
        out.writeDouble(this.pos.getY());
        out.writeDouble(this.pos.getZ());
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        int flags = 0;
        out.writeByte(flags);
        if(protocol >= 110) {
            out.writeVarIntToBuffer(this.teleport);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        final double x = in.readDouble();
        final double y = in.readDouble();
        final double z = in.readDouble();
        this.pos = new Position(x,y,z);
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        int flags = in.readUnsignedByte();
        if(protocol >= 110) {
            this.teleport = in.readVarIntFromBuffer();
        }
    }
}
