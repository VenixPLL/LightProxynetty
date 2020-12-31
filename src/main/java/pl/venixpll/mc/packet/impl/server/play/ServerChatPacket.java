package pl.venixpll.mc.packet.impl.server.play;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.data.chat.MessagePosition;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Data
public class ServerChatPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x02, 47));
        this.getProtocolList().add(new Protocol(0x0F, 110));
        this.getProtocolList().add(new Protocol(0x0F, 340));
    }

    public ServerChatPacket(String message) {
        this(message, MessagePosition.CHATBOX);
    }

    public ServerChatPacket(String message, MessagePosition position) {
        this.message = Message.fromString(LogUtil.fixColor(message));
        this.position = position;
    }

    public ServerChatPacket(BaseComponent... text) {
        this(ComponentSerializer.toString(text), MessagePosition.CHATBOX);
    }

    private Message message;
    private MessagePosition position;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(LogUtil.fixColor(message.toJsonString()));
        out.writeByte(position.getId());
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.message = Message.fromString(LogUtil.fixColor(in.readStringFromBuffer(32767)));
        this.position = MessagePosition.getById(in.readByte());
    }
}
