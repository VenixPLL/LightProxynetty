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

import lombok.SneakyThrows;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.data.game.Difficulty;
import pl.venixpll.mc.data.game.Dimension;
import pl.venixpll.mc.data.game.Gamemode;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.CustomPacket;
import pl.venixpll.mc.packet.impl.server.play.*;

import java.io.File;

public class WorldUtils {

    public static void emptyWorld(Player player) {
        dimSwitch(player, new ServerJoinGamePacket(0, Gamemode.SURVIVAL, Dimension.OVERWORLD, Difficulty.PEACEFULL, 1, "default_1_1", false));
        player.sendPacket(new ServerSpawnPositionPacket(new Position(0, 0, 0)));
        player.sendPacket(new ServerPlayerAbilitiesPacket(false, true, false, false, 2, 2));
        player.sendPacket(new ServerPlayerPosLookPacket(new Position(0, 0, 0), 180, 90, true));
    }

    public static void dimSwitch(Player player, ServerJoinGamePacket packet) {
        player.sendPacket(new ServerRespawnPacket(Dimension.END, Difficulty.PEACEFULL, Gamemode.SURVIVAL, "default_1_1"));
        player.sendPacket(packet);
        player.sendPacket(new ServerRespawnPacket(packet.getDimension(), packet.getDifficulty(), packet.getGamemode(), packet.getLevelType()));
    }

    @SneakyThrows
    public static void lobby(Player player, String fileName) {
        final NBTTagCompound tag = CompressedStreamTools.read(new File(LightProxy.class.getSimpleName(), "world/" + fileName));
        assert tag != null;
        final NBTTagCompound chunks = tag.getTagList("Chunks", 10).getCompoundTagAt(0);
        for (int i = 0; i < chunks.getKeySet().size(); i++) {
            final Packet packet = new CustomPacket();
            final byte[] data = chunks.getByteArray(String.valueOf(i));
            final int id = tag.getInteger("Id");
            packet.setCustom(id, data);
            player.sendPacket(packet);
        }
        player.sendPacket(new ServerPlayerAbilitiesPacket(false, false, true, false, 0.1f, 0.1f));
        player.sendPacket(new ServerPlayerPosLookPacket(new Position(-8, 52, 0.5), -180, 0, true));
    }
}
