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

package pl.venixpll.mc.data.item;

import net.minecraft.nbt.NBTTagCompound;

public class ItemStack {
    private int id;
    private int amount;
    private int data;
    private NBTTagCompound nbt;

    public ItemStack(final int id) {
        this.id = id;
        this.amount = 1;
    }

    public ItemStack(final int id, final int amount) {
        this.id = id;
        this.amount = amount;
        this.data = 0;
    }

    public ItemStack(final int id, final int amount, final int data) {
        this.id = id;
        this.amount = amount;
        this.data = data;
    }

    public ItemStack(final int id, final int amount, final int data, final NBTTagCompound nbt) {
        this.id = id;
        this.amount = amount;
        this.data = data;
        this.nbt = nbt;
    }

    public int getId() {
        return this.id;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getData() {
        return this.data;
    }

    public NBTTagCompound getNBT() {
        return this.nbt;
    }
}
