package pl.venixpll.mc.packet.impl.server.play;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Data
public class ServerPlayerListHeaderFooter extends Packet {

    private Message header, footer;

    {
        this.setPacketID(0x47);
    }

    public ServerPlayerListHeaderFooter(String header, String footer) {
        this.header = Message.fromString(LogUtil.fixColor(header));
        this.footer = Message.fromString(LogUtil.fixColor(footer));
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(header.toJsonString());
        out.writeString(footer.toJsonString());
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.header = Message.fromString(in.readStringFromBuffer(32767));
        this.footer = Message.fromString(in.readStringFromBuffer(32767));
    }
}
