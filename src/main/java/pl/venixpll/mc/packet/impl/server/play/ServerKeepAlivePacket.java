package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerKeepAlivePacket extends Packet {

    {
        this.getProtocolList().add(new Protocol(0x00, 47));
        this.getProtocolList().add(new Protocol(0x1F, 110));
        this.getProtocolList().add(new Protocol(0x1F, 340));
    }

    private long keepaliveId;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if(protocol >= 340) {
            out.writeLong(this.keepaliveId);
        } else {
            out.writeVarIntToBuffer((int) this.keepaliveId);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        if(protocol >= 340) {
            this.keepaliveId = in.readLong();
        } else {
            this.keepaliveId = in.readVarIntFromBuffer();
        }
    }
}
