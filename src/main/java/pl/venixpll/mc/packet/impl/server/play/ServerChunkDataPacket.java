package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.venixpll.mc.data.world.Chunk;
import pl.venixpll.mc.data.world.ChunkSection;
import pl.venixpll.mc.data.world.array.NibbleArray3d;
import pl.venixpll.mc.data.world.array.ShortArray3d;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

@Getter
public class ServerChunkDataPacket extends Packet {

    {
        this.setPacketID(0x21);
    }

    private ChunkSection chunkSection;
    private boolean biome;
    private boolean isHaveSky;
    private int bitmask;

    public ServerChunkDataPacket(ChunkSection chunkSection, boolean biome, boolean isHaveSky) {
        this.chunkSection = chunkSection;
        this.biome = biome;
        this.isHaveSky = isHaveSky;
    }

    public ServerChunkDataPacket() {
    }

    @Override
    public void write(PacketBuffer packetBuffer) throws Exception {
        packetBuffer.writeInt(chunkSection.getX());
        packetBuffer.writeInt(chunkSection.getZ());
        packetBuffer.writeBoolean(biome);

        WritedChunkSection section = WritedChunkSection.writeChunkSection(chunkSection, isHaveSky, biome);

        packetBuffer.writeShort(section.getBitmask() & 65535);
        packetBuffer.writeByteArray(section.getData());
    }

    @Override
    public void read(PacketBuffer packetBuffer) throws Exception {
        int x = packetBuffer.readInt();
        int z = packetBuffer.readInt();
        this.biome = packetBuffer.readBoolean();

        int bitmask = packetBuffer.readShort() & 65535;
        byte[] data = packetBuffer.readByteArray();
        this.bitmask = bitmask;

        ReadedChunkSection readedChunkSection = ReadedChunkSection.readChunkSection(data, bitmask, x, z, biome);
        chunkSection = readedChunkSection.getChunkSection();
        isHaveSky = readedChunkSection.skylight;
    }

    @Override
    public String toString() {
        return "ServerChunkDataPacket{" +
                "chunkSection=" + chunkSection +
                ", biome=" + biome +
                ", isHaveSky=" + isHaveSky +
                ", bitmask=" + bitmask +
                '}';
    }

    public int getX() {
        return chunkSection.getX();
    }

    public int getZ() {
        return chunkSection.getZ();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WritedChunkSection {
        private byte[] data;
        private int x,z;
        private short bitmask;

        public static WritedChunkSection writeChunkSection(ChunkSection chunkSection, boolean skylight, boolean biome) {
            short mask = 0;
            int pos = 0;
            int i;

            Chunk[] chunks = chunkSection.getAllChunks();

            for(i = 0;i < chunks.length;i++) {
                Chunk chunk = chunks[i];
                if(chunk != null) {
                    mask |= 1 << i;
                }
            }

            byte[] data = new byte[calculate_size(mask,skylight,biome)];
            ShortBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();

            for(i = 0;i < chunks.length;i++) {
                Chunk chunk = chunks[i];
                if(chunk != null) {
                    pos += chunk.getBlocks().getData().length * 2;
                    buffer.put(chunk.getBlocks().getData(), 0, chunk.getBlocks().getData().length);
                    buffer.position(pos / 2);
                }
            }

            for(i = 0;i < chunks.length;i++) {
                Chunk chunk = chunks[i];
                if(chunk != null) {
                    byte[] blocklight = chunk.getBlocklight().getData();
                    System.arraycopy(blocklight, 0, data, pos, blocklight.length);
                    pos += blocklight.length;
                }
            }


            if(skylight) {
                for (i = 0; i < chunks.length; i++) {
                    Chunk chunk = chunks[i];
                    if(chunk != null) {
                        byte[] skylightArray = chunk.getSkylight().getData();
                        System.arraycopy(skylightArray, 0, data, pos, skylightArray.length);
                        pos += skylightArray.length;
                    }
                }
            }

            if(biome) {
                System.arraycopy(chunkSection.getBiomeData(), 0, data, pos, chunkSection.getBiomeData().length);
            }

            return new WritedChunkSection(data,chunkSection.getX(),chunkSection.getZ(),mask);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    public static class ReadedChunkSection {
        private ChunkSection chunkSection;
        private int bitmask;
        private int x, z;
        private boolean skylight;

        public static ReadedChunkSection readChunkSection(byte[] data, int bitmask, int x, int z, boolean biome) {
            Chunk[] chunks = new Chunk[16];
            ShortBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            int pos = 0;

            for (int index = 0; index < 16; index++) {
                if ((bitmask & 1 << index) != 0) {
                    Chunk chunk = new Chunk();
                    chunk.setY(index);
                    ShortArray3d shortArray3d = chunk.getBlocks();
                    buffer.get(shortArray3d.getData(), 0, shortArray3d.getData().length);
                    pos += shortArray3d.getData().length * 2;
                    chunk.setBlocks(shortArray3d);
                    chunks[index] = chunk;
                } else if (biome && chunks[index] != null) {
                    chunks[index] = null;
                }
            }

            for (int index = 0; index < 16; index++) {
                if ((bitmask & 1 << index) != 1 && chunks[index] != null) {
                    Chunk chunk = chunks[index];
                    byte[] array = new byte[2048];
                    System.arraycopy(data, pos, array, 0, array.length);
                    pos += array.length;
                    chunk.setBlocklight(new NibbleArray3d(array));
                }
            }


            boolean skylight = ((4096 * 2) + 2048) * Integer.bitCount(bitmask & 65535) + (biome ? 256 : 0) < data.length;
            if (skylight) {
                for (int index = 0; index < 16; index++) {
                    if ((bitmask & 1 << index) != 1 && chunks[index] != null) {
                        Chunk chunk = chunks[index];
                        byte[] array = new byte[2048];
                        System.arraycopy(data, pos, array, 0, array.length);
                        pos += array.length;
                        chunk.setSkylight(new NibbleArray3d(array));
                    }
                }
            }

            byte[] biomeData = null;
            if (biome) {
                biomeData = new byte[256];
                if (data.length - pos >= 256) {
                    System.arraycopy(data, pos, biomeData, 0, biomeData.length);
                }
            }
            System.out.println(data.length - pos + " bytes " + data.length + " bitmask:" + Integer.bitCount(bitmask & 65535) + "\n");
            return new ReadedChunkSection(new ChunkSection(x, z, chunks, biomeData), bitmask, x, z, skylight);
        }
    }

    public static int calculate_size(int bitmask, boolean skylight, boolean biome) {
        bitmask = Integer.bitCount(bitmask & 65535);
        int i = bitmask * 2 * 16 * 16 * 16;//bloktypes array
        int j = bitmask * 16 * 16 * 16 / 2;//block light array
        int k = skylight ? bitmask * 16 * 16 * 16 / 2 : 0;//skylight array
        int l = biome ? 256 : 0;//Biome id array
        return i + j + k + l;
    }

}
