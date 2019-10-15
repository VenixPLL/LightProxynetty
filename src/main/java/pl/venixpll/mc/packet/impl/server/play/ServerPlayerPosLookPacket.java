package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerPlayerPosLookPacket extends Packet {

    private Position pos;
    private float yaw;
    private float pitch;
    private boolean onGround;

    {
        this.setPacketID(0x08);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeDouble(this.pos.getX());
        out.writeDouble(this.pos.getY());
        out.writeDouble(this.pos.getZ());
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeByte((byte)(this.onGround ? 1 : 0));
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        final double x = in.readDouble();
        final double y = in.readDouble();
        final double z = in.readDouble();
        this.pos = new Position(x,y,z);
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.onGround = in.readByte() == 1;
    }
}
