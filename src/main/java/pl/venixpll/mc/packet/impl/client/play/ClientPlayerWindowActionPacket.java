package pl.venixpll.mc.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.game.WindowAction;
import pl.venixpll.mc.data.item.ItemStack;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientPlayerWindowActionPacket extends Packet {

    private int windowId;
    private short slot;
    private int button;
    private WindowAction mode;
    private int action;
    private ItemStack item;

    {
        this.setPacketID(0x0E);
    }

    @Override
    public void write(PacketBuffer packetBuffer) throws Exception {
        packetBuffer.writeByte(this.windowId);
        packetBuffer.writeShort(this.slot);
        packetBuffer.writeByte(this.button);
        packetBuffer.writeShort(this.action);
        packetBuffer.writeByte(this.mode.getId());
        packetBuffer.writeItemStackToBuffer(this.item);
    }

    @Override
    public void read(PacketBuffer packetBuffer) throws Exception {
        this.windowId = packetBuffer.readByte();
        this.slot = packetBuffer.readShort();
        this.button = packetBuffer.readByte();
        this.action = packetBuffer.readShort();
        this.mode = WindowAction.getActionById(packetBuffer.readByte());
        this.item = packetBuffer.readItemStackFromBuffer();
    }
}