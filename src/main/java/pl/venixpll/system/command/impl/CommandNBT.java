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

package pl.venixpll.system.command.impl;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import pl.venixpll.mc.data.item.ItemStack;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.client.play.ClientCreativeInventoryAction;
import pl.venixpll.system.command.Command;

public class CommandNBT extends Command {

    public CommandNBT() {
        super(",nbt", "test", "");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final NBTTagList nbtTagList = new NBTTagList();
        nbtTagList.appendTag(new NBTTagString("TestNBT!"));
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("pages", nbtTagList);
        final ItemStack stack = new ItemStack(386, 1, 0, compound);
        sender.getConnector().sendPacket(new ClientCreativeInventoryAction(36, stack));
        sender.sendChatMessage("&cSent NBT!");
    }
}
