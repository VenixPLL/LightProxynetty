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

package pl.venixpll.system.crash;

import org.reflections.Reflections;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.system.command.CommandManager;
import pl.venixpll.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrashRegistry {

    private static List<Crash> crashList = new ArrayList<>();

    public static void init() {
        CommandManager.registerCommand(new Command(",crash", "Zzzz", "<method,list> <mArgs>") {
            @Override
            public void onExecute(String cmd, Player sender) throws Exception {
                try {
                    execute(cmd, sender);
                } catch (final Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        new Reflections("pl.venixpll.system.crash.impl").getSubTypesOf(Crash.class).forEach(crash -> {
            try {
                Crash c = crash.newInstance();
                crashList.add(c);
                c.init();
            } catch (Exception ignored) {
            }
        });
        LogUtil.printMessage("Loaded %s crashers!", crashList.size());
    }

    public static void execute(final String message, final Player sender) {
        final String[] args = message.split(" ");
        if (args[1].equalsIgnoreCase("list")) {
            crashList.forEach(c -> sender.sendChatMessage("&c,crash %s <amount> &8- &6%s", c.getName(), c.getCrashType().name()));
        } else {
            if (sender.getConnector() != null && sender.getConnector().isConnected()) {

                final Optional<Crash> crashOptional = crashList.stream().filter(c -> c.getName().equalsIgnoreCase(args[1])).findFirst();
                if (crashOptional.isPresent()) {
                    try {
                        crashOptional.get().execute(message, sender);
                    } catch (final Exception exc) {
                        sender.sendChatMessage("&cError during crashing current server!");
                    }
                } else {
                    sender.sendChatMessage("&cCrash method not found!");
                    sender.sendChatMessage("&cUse \"crash list\" for list of methods!");
                }
            } else {
                sender.sendChatMessage("&cYou have to be connected!");
            }
        }
    }

}
