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


import java.util.Objects;

public class HoverEvent implements Cloneable {
    private HoverAction action;
    private Message value;

    public HoverEvent(HoverAction action, Message value) {
        this.action = action;
        this.value = value;
    }

    public HoverAction getAction() {
        return this.action;
    }

    public Message getValue() {
        return this.value;
    }

    @Override
    public HoverEvent clone() {
        return new HoverEvent(this.action, this.value.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoverEvent)) return false;

        HoverEvent that = (HoverEvent) o;
        return this.action == that.action &&
                Objects.equals(this.value, that.value);
    }

}
