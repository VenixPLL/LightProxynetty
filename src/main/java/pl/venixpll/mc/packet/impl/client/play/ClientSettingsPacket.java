package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientSettingsPacket extends Packet {

    private String locale;
    private byte viewDistance;
    private byte chatMode;
    private boolean chatColors;
    private byte skinParts;

    {
        this.setPacketID(0x15);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(locale);
        out.writeByte(viewDistance);
        out.writeByte(chatMode);
        out.writeBoolean(chatColors);
        out.writeByte(skinParts);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.locale = in.readStringFromBuffer(16);
        this.viewDistance = in.readByte();
        this.chatMode = in.readByte();
        this.chatColors = in.readBoolean();
        this.skinParts = in.readByte();
    }
}
