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
import com.google.gson.JsonPrimitive;

import java.util.Objects;

public class TextMessage extends Message {
    private String text;

    public TextMessage(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public TextMessage clone() {
        return (TextMessage) new TextMessage(this.getText()).setStyle(this.getStyle().clone()).setExtra(this.getExtra());
    }

    @Override
    public JsonElement toJson() {
        if (this.getStyle().isDefault() && this.getExtra().isEmpty()) {
            return new JsonPrimitive(this.text);
        } else {
            JsonElement e = super.toJson();
            if (e.isJsonObject()) {
                JsonObject json = e.getAsJsonObject();
                json.addProperty("text", this.text);
                return json;
            } else {
                return e;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextMessage)) return false;

        TextMessage that = (TextMessage) o;
        return super.equals(o) &&
                Objects.equals(this.text, that.text);
    }
}
