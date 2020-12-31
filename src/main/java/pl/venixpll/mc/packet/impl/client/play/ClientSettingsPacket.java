package pl.venixpll.mc.packet.impl.client.play;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@NoArgsConstructor
@Data
public class ClientSettingsPacket extends Packet {


    {
        this.getProtocolList().add(new Protocol(0x15, 47));
        this.getProtocolList().add(new Protocol(0x15, 110));
        this.getProtocolList().add(new Protocol(0x04, 340));
    }

    public ClientSettingsPacket(String locale, byte viewDistance, byte chatMode, boolean chatColors, byte skinParts) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatMode = chatMode;
        this.chatColors = chatColors;
        this.skinParts = skinParts;
    }

    public ClientSettingsPacket(String locale, byte viewDistance, byte chatMode, boolean chatColors, byte skinParts, int mainHand) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatMode = chatMode;
        this.chatColors = chatColors;
        this.skinParts = skinParts;
        this.mainHand = mainHand;
    }

    private String locale;
    private byte viewDistance;
    private byte chatMode;
    private boolean chatColors;
    private byte skinParts;
    private int mainHand;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(locale);
        out.writeByte(viewDistance);
        out.writeByte(chatMode);
        out.writeBoolean(chatColors);
        out.writeByte(skinParts);
        if(protocol >= 110) {
            out.writeVarIntToBuffer(mainHand);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.locale = in.readStringFromBuffer(16);
        this.viewDistance = in.readByte();
        this.chatMode = in.readByte();
        this.chatColors = in.readBoolean();
        this.skinParts = in.readByte();
        if(protocol >= 110) {
            this.mainHand = in.readVarIntFromBuffer();
        }
    }
}
