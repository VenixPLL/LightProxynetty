package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerSpawnPositionPacket extends Packet {

    {
        this.setPacketID(0x05);
    }

    private Position position;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writePosition(this.position);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.position = in.readPosition();
    }
}
