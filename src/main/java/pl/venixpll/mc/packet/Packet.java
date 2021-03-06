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

package pl.venixpll.mc.packet;

import lombok.Data;

@Data
public abstract class Packet {

    private int packetID;
    private byte[] customData;
    private boolean custom;

    public abstract void write(PacketBuffer out) throws Exception;

    public abstract void read(PacketBuffer in) throws Exception;

    public void setCustom(int id, byte[] data) {
        this.custom = true;
        this.packetID = id;
        this.customData = data;
    }

}
