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

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.utils.Util;

import java.io.File;

public class CommandChunks extends Command {
    public CommandChunks() {
        super(",chunks","Listening and saving chunks to file.","<listen,save> <file>");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final String[] args = cmd.split(" ");
        if (args[1].equalsIgnoreCase("listen")){
            sender.sendChatMessage("&6Listening chunks %s", sender.isListenChunks() ? "&cFALSE" : "&aTRUE");
            sender.setListenChunks(!sender.isListenChunks());
        }else if (args[1].equalsIgnoreCase("save")){

            if (args.length != 3) {
                sender.sendChatMessage("&cYou must select name of file to save chunks.");
                return;
            }

            if (sender.isListenChunks()){
                sender.sendChatMessage("&cTo save chunks you must turn off listening!");
                return;
            }

            if (sender.getBytes().size() < 1){
                sender.sendChatMessage("&cYou don't have any saved chunks!");
                return;
            }
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("Id", 0x26);
            final NBTTagList listTag = new NBTTagList();
            final NBTTagCompound chunks = new NBTTagCompound();
            for (int i = 0; i < sender.getBytes().size(); i++) {
                chunks.setByteArray(String.valueOf(i), sender.getBytes().get(i));
            }
            listTag.appendTag(chunks);
            tag.setTag("Chunks", listTag);
            CompressedStreamTools.write(tag, new File(LightProxy.class.getSimpleName() + "/world/" + args[2] + ".dat"));
            sender.getBytes().clear();
            sender.sendChatMessage("&6Chunks saved in file &a" + args[2] + ".dat");
        }
    }
}
