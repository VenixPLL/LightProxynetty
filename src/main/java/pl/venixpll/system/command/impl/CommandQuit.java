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
import pl.venixpll.system.command.Command;

public class CommandQuit extends Command {

    public CommandQuit() {
        super(",q", "Disconnecting from server", "<bots(op)>");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final String[] args = cmd.split(" ");
        if(args.length >= 2){
            if(args[1].equalsIgnoreCase("bots")){
                if(sender.getBots().isEmpty()){
                    sender.sendChatMessage("&cYou do not have any bots!");
                }else{
                    sender.getBots().forEach(bot -> {
                        bot.getConnection().close();
                    });
                    sender.sendChatMessage("&cDisconnected all bots!");
                }
                return;
            }
        }
        if(sender.getConnector() != null && sender.getConnector().isConnected()){
            sender.sendChatMessage("&cDisconnected!");
            sender.getConnector().close();
        }else if(sender.getConnector() != null && !sender.getConnector().isConnected()){
            sender.sendChatMessage("&cYou are not connected!");
        }else if(sender.getConnector() == null){
            sender.sendChatMessage("&cYou are not connected!");
        }
    }
}
