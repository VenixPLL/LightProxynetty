package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerKeepAlivePacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    private int keepaliveId;

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.keepaliveId);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.keepaliveId = in.readVarIntFromBuffer();
    }
}
