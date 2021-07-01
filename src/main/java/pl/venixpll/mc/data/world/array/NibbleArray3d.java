package pl.venixpll.mc.data.world.array;

import java.util.Arrays;

public class NibbleArray3d {

    private final byte[] data;

    public NibbleArray3d(int size) {
        this.data = new byte[size];
    }

    public NibbleArray3d(byte[] array) {
        this.data = array;
    }

    public byte[] getData() {
        return this.data;
    }

    public int get(int x, int y, int z) {
        int key = y << 8 | z << 4 | x;
        int index = key >> 1;
        int part = key & 1;
        return part == 0 ? this.data[index] & 15 : this.data[index] >> 4 & 15;
    }

    public void set(int x, int y, int z, int val) {
        int key = y << 8 | z << 4 | x;
        int index = key >> 1;
        int part = key & 1;
        if(part == 0) {
            this.data[index] = (byte) (this.data[index] & 240 | val & 15);
        } else {
            this.data[index] = (byte) (this.data[index] & 15 | (val & 15) << 4);
        }
    }

    public void fill(int val) {
        for(int index = 0; index < this.data.length << 1; index++) {
            int ind = index >> 1;
            int part = index & 1;
            if(part == 0) {
                this.data[ind] = (byte) (this.data[ind] & 240 | val & 15);
            } else {
                this.data[ind] = (byte) (this.data[ind] & 15 | (val & 15) << 4);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        NibbleArray3d that = (NibbleArray3d) o;

        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

}
