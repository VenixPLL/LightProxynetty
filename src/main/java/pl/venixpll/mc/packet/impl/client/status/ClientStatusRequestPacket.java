package pl.venixpll.mc.packet.impl.client.status;

import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

public class ClientStatusRequestPacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {}

    @Override
    public void read(PacketBuffer in) throws Exception {}
}
