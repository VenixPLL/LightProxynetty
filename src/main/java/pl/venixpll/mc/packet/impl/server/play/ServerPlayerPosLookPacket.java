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

package pl.venixpll.mc.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerPlayerPosLookPacket extends Packet {

    private Position pos;
    private float yaw;
    private float pitch;
    private boolean onGround;

    {
        this.setPacketID(0x08);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeDouble(this.pos.getX());
        out.writeDouble(this.pos.getY());
        out.writeDouble(this.pos.getZ());
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeByte((byte) (this.onGround ? 1 : 0));
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        final double x = in.readDouble();
        final double y = in.readDouble();
        final double z = in.readDouble();
        this.pos = new Position(x, y, z);
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.onGround = in.readByte() == 1;
    }
}
