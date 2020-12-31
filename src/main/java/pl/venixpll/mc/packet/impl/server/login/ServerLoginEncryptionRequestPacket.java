package pl.venixpll.mc.packet.impl.server.login;

import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

public class ServerLoginEncryptionRequestPacket extends Packet {
    {
        this.getProtocolList().add(new Protocol(0x01, 47));
    }


    private byte[] data;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        ///tururur
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        data = new byte[in.readableBytes()];
        in.readBytes(data);
    }
}
