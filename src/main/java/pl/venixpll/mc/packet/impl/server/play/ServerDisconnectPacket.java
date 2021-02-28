package pl.venixpll.mc.packet.impl.server.play;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.utils.LogUtil;

@Data
@NoArgsConstructor
public class ServerDisconnectPacket extends Packet {

    private Message reason;

    {
        this.setPacketID(0x40);
    }

    public ServerDisconnectPacket(String reason) {
        this.reason = Message.fromString(LogUtil.fixColor(reason));
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(reason.toJsonString());
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.reason = Message.fromString(in.readStringFromBuffer(32767));
    }
}
