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

package pl.venixpll.mc.packet.impl.handshake;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HandshakePacket extends Packet {

    private int protocolId;
    private String host;
    private int port;
    private int nextState;

    {
        this.setPacketID(0x00);
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(this.protocolId);
        out.writeString(this.host);
        out.writeShort(this.port);
        out.writeVarIntToBuffer(this.nextState);
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.protocolId = in.readVarIntFromBuffer();
        this.host = in.readStringFromBuffer(128);
        this.port = in.readShort();
        this.nextState = in.readVarIntFromBuffer();
    }
}
