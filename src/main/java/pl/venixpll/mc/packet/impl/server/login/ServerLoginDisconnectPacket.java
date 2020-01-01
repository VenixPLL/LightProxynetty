package pl.venixpll.mc.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerLoginDisconnectPacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    private Message reason;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(reason.toJsonString());
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.reason = Message.fromString(in.readStringFromBuffer(32767));
    }
}
