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

package pl.venixpll.utils;

public final class WaitTimer {
    public long time;

    public WaitTimer() {
        this.time = (System.nanoTime() / 1000000l);
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (getTime() >= time) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.nanoTime() / 1000000l - this.time;
    }

    public void reset() {
        this.time = (System.nanoTime() / 1000000l);
    }
}