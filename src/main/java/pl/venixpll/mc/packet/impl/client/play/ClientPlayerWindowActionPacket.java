package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.game.WindowAction;
import pl.venixpll.mc.data.item.ItemStack;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.mc.packet.Protocol;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientPlayerWindowActionPacket extends Packet {


    {
        this.getProtocolList().add(new Protocol(0x0E, 47));
        this.getProtocolList().add(new Protocol(0x07, 110));
        this.getProtocolList().add(new Protocol(0x07, 340));
    }

    private int windowId;
    private short slot;
    private int button;
    private WindowAction mode;
    private int action;
    private ItemStack item;

    @Override
    public void write(PacketBuffer packetBuffer, int protocol) throws Exception {
        packetBuffer.writeByte(this.windowId);
        packetBuffer.writeShort(this.slot);
        packetBuffer.writeByte(this.button);
        packetBuffer.writeShort(this.action);
        if(protocol >= 110) {
            packetBuffer.writeVarIntToBuffer(this.mode.getId());
        } else {
            packetBuffer.writeByte(this.mode.getId());
        }
        packetBuffer.writeItemStackToBuffer(this.item);
    }

    @Override
    public void read(PacketBuffer packetBuffer, int protocol) throws Exception {
        this.windowId = packetBuffer.readByte();
        this.slot = packetBuffer.readShort();
        this.button = packetBuffer.readByte();
        this.action = packetBuffer.readShort();
        if(protocol >= 110) {
            this.mode = WindowAction.getActionById(packetBuffer.readVarIntFromBuffer());
        } else {
            this.mode = WindowAction.getActionById(packetBuffer.readByte());
        }
        this.item = packetBuffer.readItemStackFromBuffer();
    }
}