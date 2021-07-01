package pl.venixpll.mc.data.world;

import lombok.Data;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.data.world.array.NibbleArray3d;
import pl.venixpll.mc.data.world.array.ShortArray3d;

@Data
public class Chunk {
    private ShortArray3d blocks;
    private NibbleArray3d blocklight;
    private NibbleArray3d skylight;
    private int y;

    public Chunk() {
        this(new ShortArray3d(4096),new NibbleArray3d(2048),new NibbleArray3d(2048));
    }

    public Chunk(ShortArray3d blocks,NibbleArray3d blocklight, NibbleArray3d skylight) {
        this.blocks = blocks;
        this.blocklight = blocklight;
        this.skylight = skylight;
    }

    public boolean isEmpty() {
        for(short block : this.blocks.getData()) {
            if(block != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isIn(Position pos) {
        return pos.getY() > y * 16 && pos.getY() < y * 16 + 16;
    }

//    @Override
//    public String toString() {
//        return "Chunk{" +
//                "blocks=" + blocks +
//                ", blocklight=" + blocklight +
//                ", skylight=" + skylight +
//                ", y=" + y +
//                '}';
//    }
}
