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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

public class KeybindMessage extends Message {
    private String keybind;

    public KeybindMessage(String keybind) {
        this.keybind = keybind;
    }

    public String getKeybind() {
        return this.keybind;
    }

    @Override
    public String getText() {
        return this.keybind;
    }

    @Override
    public KeybindMessage clone() {
        return (KeybindMessage) new KeybindMessage(this.getKeybind()).setStyle(this.getStyle().clone()).setExtra(this.getExtra());
    }

    @Override
    public JsonElement toJson() {
        JsonElement e = super.toJson();
        if (e.isJsonObject()) {
            JsonObject json = e.getAsJsonObject();
            json.addProperty("keybind", this.keybind);
            return json;
        } else {
            return e;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeybindMessage)) return false;

        KeybindMessage that = (KeybindMessage) o;
        return super.equals(o) &&
                Objects.equals(this.keybind, that.keybind);
    }
}
