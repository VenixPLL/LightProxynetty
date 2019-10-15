package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientChatPacket extends Packet {

    {
        this.setPacketID(0x01);
    }

    private String message;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(this.message);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.message = in.readStringFromBuffer(32767);
    }
}
