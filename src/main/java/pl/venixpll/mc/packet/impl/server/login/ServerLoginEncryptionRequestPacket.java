package pl.venixpll.mc.packet.impl.server.login;

import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

public class ServerLoginEncryptionRequestPacket extends Packet {

    private byte[] data;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        ///tururur
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        data = new byte[in.readableBytes()];
        in.readBytes(data);
    }
}
