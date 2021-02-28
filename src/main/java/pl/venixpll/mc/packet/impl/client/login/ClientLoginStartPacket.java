package pl.venixpll.mc.packet.impl.client.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
public class ClientLoginStartPacket extends Packet {

    private String username;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.username = in.readStringFromBuffer(32);
    }
}
