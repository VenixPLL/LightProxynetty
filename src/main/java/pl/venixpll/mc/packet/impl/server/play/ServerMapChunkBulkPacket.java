package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.world.ChunkSection;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ServerMapChunkBulkPacket extends Packet {
    private ArrayList<ChunkSection> chunks = new ArrayList<>();
    private boolean isSky;

    {
        this.setPacketID(0x26);
    }

    @Override
    public void write(PacketBuffer packetBuffer) throws Exception {
        packetBuffer.writeBoolean(isSky);
        packetBuffer.writeVarIntToBuffer(chunks.size());
        ServerChunkDataPacket.WritedChunkSection[] sections = new ServerChunkDataPacket.WritedChunkSection[chunks.size()];

        int index = 0;
        for(ChunkSection section:chunks) {
            sections[index] = ServerChunkDataPacket.WritedChunkSection.writeChunkSection(section,isSky,true);
            index++;
        }

        for(ServerChunkDataPacket.WritedChunkSection section:sections) {
            packetBuffer.writeInt(section.getX());
            packetBuffer.writeInt(section.getZ());
            packetBuffer.writeShort(section.getBitmask());
        }
        for(ServerChunkDataPacket.WritedChunkSection section:sections) {
            packetBuffer.writeBytes(section.getData());
        }
    }

    @Override
    public void read(PacketBuffer packetBuffer) throws Exception {
        this.isSky = packetBuffer.readBoolean();
        int size = packetBuffer.readVarIntFromBuffer();
        ServerChunkDataPacket.ReadedChunkSection[] readedChunkSections = new ServerChunkDataPacket.ReadedChunkSection[size];


        int[] posX = new int[size];
        int[] posZ = new int[size];
        int[] bitmask = new int[size];

        for(int index = 0;index < readedChunkSections.length;index++) {
            posX[index] = packetBuffer.readInt();
            posZ[index] = packetBuffer.readInt();
            bitmask[index] = packetBuffer.readShort();
        }

        for(int index = 0;index < readedChunkSections.length;index++) {
            int dataSize = ServerChunkDataPacket.calculate_size(bitmask[index],isSky,true);
            byte[] data = new byte[dataSize];
            packetBuffer.readBytes(data);
            ServerChunkDataPacket.ReadedChunkSection readedChunkSection = ServerChunkDataPacket.ReadedChunkSection.readChunkSection(data,bitmask[index],posX[index],posZ[index],true);
            readedChunkSections[index] = readedChunkSection;
        }

        for(ServerChunkDataPacket.ReadedChunkSection section:readedChunkSections) {
            chunks.add(section.getChunkSection());
        }

    }

    @Override
    public String toString() {
        return "ServerMapChunkBulkPacket{" +
                "chunks=" + chunks +
                ", isSky=" + isSky +
                '}';
    }
}
