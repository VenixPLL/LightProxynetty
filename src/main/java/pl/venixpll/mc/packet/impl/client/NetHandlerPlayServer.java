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
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.play.ClientChatPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientKeepAlivePacket;
import pl.venixpll.system.command.CommandManager;
import pl.venixpll.utils.LogUtil;

@RequiredArgsConstructor
@Data
public class NetHandlerPlayServer implements INetHandler {

    private final Player player;

    @Override
    public void disconnected() {
        LogUtil.printMessage("[%s] Disconnected.", player.getUsername());
    }

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof ClientKeepAlivePacket) {
            final int time = ((ClientKeepAlivePacket) packet).getTime();
            player.setPing((int) (System.currentTimeMillis() - time));
        } else if (packet instanceof ClientChatPacket) {
            final String message = ((ClientChatPacket) packet).getMessage();
            if (message.startsWith(",")) {
                CommandManager.onCommand(message, player);
            } else if (message.startsWith("@")) {
                LightProxy.getServer().getPlayerList().forEach(p -> p.sendChatMessageNoPrefix("&6" + player.getUsername() + " &8» &e" + message.substring(1)));
            } else {
                forwardPacket(packet);
            }
        } else {
            forwardPacket(packet);
        }
    }

    private void forwardPacket(final Packet packet) {
        if (player.getConnector() != null && player.getConnector().isConnected()) {
            if (player.isMother()) {
                player.getBots().forEach(bot -> {
                    if (bot.getConnection().isConnected()) {
                        //TODO add Entity Action exclude;
                        bot.getConnection().sendPacket(packet);
                    }
                });
            }
            player.getConnector().sendPacket(packet);
        }
    }
}
