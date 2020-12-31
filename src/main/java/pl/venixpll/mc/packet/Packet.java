package pl.venixpll.mc.packet;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Packet {
    private List<Protocol> protocolList = new ArrayList<>();

    public abstract void write(PacketBuffer out, int protocol) throws Exception;

    public abstract void read(PacketBuffer in, int protocol) throws Exception;
}