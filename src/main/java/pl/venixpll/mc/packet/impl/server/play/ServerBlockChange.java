package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.data.world.Block;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ServerBlockChange extends Packet {

    {
        this.setPacketID(0x23);
    }

    private Position location;
    private Block block;

    @Override
    public void write(PacketBuffer packetBuffer) throws Exception {
        packetBuffer.writePosition(location);
        packetBuffer.writeVarIntToBuffer(block.getId() << 4 | (block.getData() & 0xF));
    }

    @Override
    public void read(PacketBuffer packetBuffer) throws Exception {
        this.location = packetBuffer.readPosition();
        int data = packetBuffer.readVarIntFromBuffer();
        this.block = new Block(data >> 4,data & 0xF);
    }
}
