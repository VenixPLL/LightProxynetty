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

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.data.game.TitleAction;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;
import pl.venixpll.utils.LogUtil;

@NoArgsConstructor
@Getter
public class ServerTitlePacket extends Packet {

    private TitleAction titleAction;
    private Message title;
    private Message subTitle;
    private int fadeIn;
    private int fadeOut;
    private int stay;

    {
        this.setPacketID(0x45);
    }

    public ServerTitlePacket(TitleAction action, String message) {
        this.titleAction = action;
        if (action == TitleAction.TITLE) {
            this.title = Message.fromString(LogUtil.fixColor(message));
        } else if (action == TitleAction.SUBTITLE) {
            this.subTitle = Message.fromString(LogUtil.fixColor(message));
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket!");
        }
    }

    public ServerTitlePacket(TitleAction action, int fadeIn, int stay, int fadeOut) {
        this.titleAction = action;
        if (titleAction == TitleAction.TIMES) {
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket");
        }
    }

    public ServerTitlePacket(TitleAction action) {
        this.titleAction = action;
    }


    @Override
    public void write(PacketBuffer out) throws Exception {
        out.writeVarIntToBuffer(titleAction.getId());
        switch (titleAction) {
            case TITLE:
                out.writeString(this.title.toJsonString());
                break;
            case SUBTITLE:
                out.writeString(this.subTitle.toJsonString());
                break;
            case TIMES:
                out.writeInt(this.fadeIn);
                out.writeInt(this.stay);
                out.writeInt(this.fadeOut);
                break;
            default:
                break;
        }
    }

    @Override
    public void read(PacketBuffer in) throws Exception {
        this.titleAction = TitleAction.getById(in.readVarIntFromBuffer());
        switch (titleAction) {
            case TITLE:
                this.title = Message.fromString(in.readStringFromBuffer(32767));
                break;
            case SUBTITLE:
                this.subTitle = Message.fromString(in.readStringFromBuffer(32767));
                break;
            case TIMES:
                this.fadeIn = in.readInt();
                this.stay = in.readInt();
                this.fadeOut = in.readInt();
                break;
            default:
                break;
        }
    }
}
