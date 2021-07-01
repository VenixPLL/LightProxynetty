package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.venixpll.mc.data.world.Block;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ServerMultiBlockChange extends Packet {

    {
        this.setPacketID(0x22);
    }

    private int x;
    private int z;
    private BlockChangeData[] blocks;

    @Override
    public void write(PacketBuffer packetBuffer) throws Exception {
        packetBuffer.writeInt(x);
        packetBuffer.writeInt(z);
        packetBuffer.writeVarIntToBuffer(blocks.length);

        for (BlockChangeData block : blocks) {
            packetBuffer.writeByte(block.x << 4 | block.z & 0xF);
            packetBuffer.writeByte(block.y);
            packetBuffer.writeByte(block.getBlock().getId() >> 4 | block.getBlock().getData() & 0xF);
        }
    }

    @Override
    public void read(PacketBuffer packetBuffer) throws Exception {
        this.x = packetBuffer.readInt();
        this.z = packetBuffer.readInt();
        int length = packetBuffer.readVarIntFromBuffer();

        blocks = new BlockChangeData[length];
        for (int index = 0; index < blocks.length; index++) {
            byte positionData = packetBuffer.readByte();
            int y = packetBuffer.readByte();
            int blockData = packetBuffer.readVarIntFromBuffer();
            blocks[index] = new BlockChangeData(positionData >> 4,y,positionData & 0xF,new Block(blockData >> 4,blockData & 0xF));
        }
    }

    @AllArgsConstructor
    @Setter
    @Getter
    public static class BlockChangeData {
        private int x,y,z;
        private Block block;
    }
}
