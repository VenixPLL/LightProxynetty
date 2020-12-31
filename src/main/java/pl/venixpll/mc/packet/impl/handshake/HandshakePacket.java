package pl.venixpll.mc.packet.impl.handshake;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HandshakePacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x00, 0));
    }

    private int protocolId;
    private String host;
    private int port;
    private int nextState;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarIntToBuffer(this.protocolId);
        out.writeString(this.host);
        out.writeShort(this.port);
        out.writeVarIntToBuffer(this.nextState);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.protocolId = in.readVarIntFromBuffer();
        this.host = in.readStringFromBuffer(128);
        this.port = in.readShort();
        this.nextState = in.readVarIntFromBuffer();
    }
}
