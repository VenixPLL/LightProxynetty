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
import pl.venixpll.mc.data.chat.MessagePosition;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Data
public class ServerChatPacket extends Packet {

    private Message message;
    private MessagePosition position;

    {
        this.setPacketID(0x02);
    }

    public ServerChatPacket(String message) {
        this(message, MessagePosition.CHATBOX);
    }
    public ServerChatPacket(String message, MessagePosition position) {
        this.message = Message.fromString(LogUtil.fixColor(message));
        this.position = position;
    }

    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeString(message.toJsonString());
        out.writeByte(position.getId());
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.message = Message.fromString(in.readStringFromBuffer(32767));
        this.position = MessagePosition.getById(in.readByte());
    }
}
