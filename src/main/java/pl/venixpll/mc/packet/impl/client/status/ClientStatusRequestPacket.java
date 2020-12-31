package pl.venixpll.mc.packet.impl.client.status;

import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

public class ClientStatusRequestPacket extends Packet {


    {
        this.getProtocolList().add(new Protocol(0x00, 47));
        this.getProtocolList().add(new Protocol(0x00, 110));
        this.getProtocolList().add(new Protocol(0x00, 340));
    }
    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {}

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {}
}
