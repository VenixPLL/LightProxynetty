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
