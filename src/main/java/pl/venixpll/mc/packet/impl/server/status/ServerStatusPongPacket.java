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

package pl.venixpll.mc.packet.impl.server.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ServerStatusPongPacket extends Packet {

    private long time;

    {
        this.setPacketID(0x01);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeLong(this.time);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.time = in.readLong();
    }
}
