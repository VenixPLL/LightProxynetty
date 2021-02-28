package pl.venixpll.mc.packet;

import lombok.Data;

@Data
public abstract class Packet {

    private int packetID;
    private byte[] customData;
    private boolean custom;

    public abstract void write(PacketBuffer out) throws Exception;

    public abstract void read(PacketBuffer in) throws Exception;

    public void setCustom(int id, byte[] data) {
        this.custom = true;
        this.packetID = id;
        this.customData = data;
    }

}
