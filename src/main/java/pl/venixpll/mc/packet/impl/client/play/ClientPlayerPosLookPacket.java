package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientPlayerPosLookPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x06, 47));
    }

    private Position position;
    private float yaw,pitch;
    private boolean onGround;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeDouble(position.getX());
        out.writeDouble(position.getY());
        out.writeDouble(position.getZ());
        out.writeFloat(yaw);
        out.writeFloat(pitch);
        out.writeBoolean(onGround);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.position = new Position();
        position.setX(in.readDouble());
        position.setY(in.readDouble());
        position.setZ(in.readDouble());
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.onGround = in.readBoolean();
    }
}
