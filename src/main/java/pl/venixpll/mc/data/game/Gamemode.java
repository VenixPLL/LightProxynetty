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

package pl.venixpll.mc.data.game;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gamemode {

    SURVIVAL(0), CREATIVE(1), ADVENTURE(2), SPECTATOR(3), HARDCORE(0x8);

    private int id;

    Gamemode(int id) {
        this.id = id;
    }

    public static Gamemode getById(int id) {
        return Arrays.asList(Gamemode.values()).stream().filter(gm -> gm.id == id).findFirst().orElse(Gamemode.SURVIVAL);
    }
}
