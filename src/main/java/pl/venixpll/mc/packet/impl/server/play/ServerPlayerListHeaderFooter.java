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

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Data
public class ServerPlayerListHeaderFooter extends Packet {

    private Message header, footer;

    {
        this.setPacketID(0x47);
    }

    public ServerPlayerListHeaderFooter(String header, String footer) {
        this.header = Message.fromString(LogUtil.fixColor(header));
        this.footer = Message.fromString(LogUtil.fixColor(footer));
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(header.toJsonString());
        out.writeString(footer.toJsonString());
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.header = Message.fromString(in.readStringFromBuffer(32767));
        this.footer = Message.fromString(in.readStringFromBuffer(32767));
    }
}
