package pl.venixpll.mc.data.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.venixpll.mc.data.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public class ChunkSection {
    int x,z;
    Chunk[] chunks;
    private byte[] biomeData;

    public boolean isSameCoordinate(ChunkSection chunk) {
        return chunk.getX() == this.getX() && chunk.getZ() == this.getZ();
    }

    public boolean isIn(Position pos) {
        return pos.getX() > x * 16 && pos.getX() < x * 16 + 16 && pos.getZ() > z * 16 && pos.getZ() < z * 16 + 16;
    }

    public Chunk[] getChunks() {
        List<Chunk> chunks0 = new ArrayList<>();
        for (Chunk chunk : this.chunks) {
            if(chunk != null) {
                chunks0.add(chunk);
            }
        }
        return chunks0.toArray(new Chunk[0]);
    }

    public Chunk[] getAllChunks() {
        return chunks;
    }

    @Override
    public String toString() {
        return "ChunkSection{" +
                "x=" + x +
                ", z=" + z +
                ", chunks=" + Arrays.toString(chunks) +
                '}';
    }
}
