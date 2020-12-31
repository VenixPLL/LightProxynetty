package pl.venixpll.mc.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerLoginDisconnectPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x00, 47));
        this.getProtocolList().add(new Protocol(0x00, 110));
        this.getProtocolList().add(new Protocol(0x00, 340));
    }

    private Message reason;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(reason.toJsonString());
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.reason = Message.fromString(in.readStringFromBuffer(32767));
    }
}
