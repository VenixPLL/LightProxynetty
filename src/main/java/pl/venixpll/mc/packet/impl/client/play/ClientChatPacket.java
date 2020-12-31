package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientChatPacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x01, 47));
        this.getProtocolList().add(new Protocol(0x02, 110));
        this.getProtocolList().add(new Protocol(0x02, 340));
    }

    private String message;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if(protocol >= 110) {
            if (message.length() > 100) {
                message = message.substring(0, 100);
            }
        }
        out.writeString(message);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.message = in.readStringFromBuffer(32767);
    }
}
