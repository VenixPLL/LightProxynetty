package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientPlayerPosLookPacket extends Packet {

    private Position position;
    private float yaw, pitch;
    private boolean onGround;

    {
        this.setPacketID(0x06);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeDouble(position.getX());
        out.writeDouble(position.getY());
        out.writeDouble(position.getZ());
        out.writeFloat(yaw);
        out.writeFloat(pitch);
        out.writeBoolean(onGround);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.position = new Position();
        position.setX(in.readDouble());
        position.setY(in.readDouble());
        position.setZ(in.readDouble());
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.onGround = in.readBoolean();
    }
}
