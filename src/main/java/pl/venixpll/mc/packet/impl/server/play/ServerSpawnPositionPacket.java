package pl.venixpll.mc.packet.impl.server.play;

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
public class ServerSpawnPositionPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x05, 47));
        this.getProtocolList().add(new Protocol(0x43, 110));
        this.getProtocolList().add(new Protocol(0x46, 340));
    }

    private Position position;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writePosition(this.position);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.position = in.readPosition();
    }
}
