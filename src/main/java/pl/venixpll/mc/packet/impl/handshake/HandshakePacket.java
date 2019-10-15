package pl.venixpll.mc.packet.impl.handshake;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HandshakePacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    private int protocolId;
    private String host;
    private int port;
    private int nextState;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.protocolId);
        out.writeString(this.host);
        out.writeShort(this.port);
        out.writeVarIntToBuffer(this.nextState);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.protocolId = in.readVarIntFromBuffer();
        this.host = in.readStringFromBuffer(128);
        this.port = in.readShort();
        this.nextState = in.readVarIntFromBuffer();
    }
}
