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
public class ClientEntityActionPacket extends Packet {

    private int entityId, actionId, actionParameter;

    {
        this.setPacketID(0x0B);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(entityId);
        out.writeVarIntToBuffer(actionId);
        out.writeVarIntToBuffer(actionParameter);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.entityId = in.readVarIntFromBuffer();
        this.actionId = in.readVarIntFromBuffer();
        this.actionParameter = in.readVarIntFromBuffer();
    }
}
