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

package pl.venixpll.mc.data.chat;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum MessagePosition {

    CHATBOX(0), SYSTEM(1), HOTBAR(2);

    private int id;

    MessagePosition(int id) {
        this.id = id;
    }

    public static MessagePosition getById(int id) {
        Optional<MessagePosition> positionOptional = Arrays.stream(MessagePosition.values()).filter(pos -> pos.getId() == id).findFirst();
        if (positionOptional.isPresent()) {
            return positionOptional.get();
        } else {
            return CHATBOX;
        }
    }
}
