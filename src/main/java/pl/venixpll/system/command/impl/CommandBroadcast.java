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

import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.client.play.ClientChatPacket;
import pl.venixpll.system.command.Command;

public class CommandBroadcast extends Command {

    public CommandBroadcast() {
        super(",bc", "Bot stuff", "<message>");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final String message = cmd.split(",bc ",2)[1];
        if(sender.getBots().isEmpty()){
            sender.sendChatMessage("&cYou do not have any bots!");
        }else{
            sender.getBots().forEach(bot -> {
                if(bot.getConnection().isConnected()){
                    bot.getConnection().sendPacket(new ClientChatPacket(message));
                }
            });
            sender.sendChatMessage("&cSent message!");
        }
    }
}
