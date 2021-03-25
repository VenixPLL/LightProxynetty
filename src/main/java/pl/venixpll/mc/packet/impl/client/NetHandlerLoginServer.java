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

package pl.venixpll.mc.packet.impl.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSuccessPacket;
import pl.venixpll.utils.LogUtil;
import pl.venixpll.utils.WorldUtils;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class NetHandlerLoginServer implements INetHandler {

    private final Player player;

    @Override
    public void disconnected() {
        LogUtil.printMessage("[%s] Disconnected during login sequence!", player.getUsername());
    }

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof ClientLoginStartPacket) {
            player.setUsername(((ClientLoginStartPacket) packet).getUsername());
            player.setCompressionThreshold(256);
            player.sendPacket(new ServerLoginSuccessPacket(UUID.randomUUID(), player.getUsername()));
            LogUtil.printMessage("[%s] Connected!", player.getUsername());
            player.setConnectionState(EnumConnectionState.PLAY);
            player.setPacketHandler(new NetHandlerPlayServer(player));
            WorldUtils.emptyWorld(player);
            WorldUtils.lobby(player, "Lobby.dat");
            LightProxy.getServer().playerList.add(player);
        }
    }
}
