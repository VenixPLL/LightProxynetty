/*
 * LightProxy
 * Copyright (C) 2021.  VenixPLL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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