package pl.venixpll.mc.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServerLoginSuccessPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x02, 47));
        this.getProtocolList().add(new Protocol(0x02, 110));
        this.getProtocolList().add(new Protocol(0x02, 340));
    }

    private UUID uuid;
    private String username;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.uuid == null ? "" : this.uuid.toString());
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.uuid = UUID.fromString(in.readStringFromBuffer(86));
        this.username = in.readStringFromBuffer(32);
    }
}
