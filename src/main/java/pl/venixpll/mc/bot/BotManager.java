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

package pl.venixpll.mc.bot;

import pl.venixpll.mc.connection.ServerPinger;
import pl.venixpll.mc.objects.Bot;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.utils.Util;

import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BotManager {

    public static void connectSome(final String host, final int port, final String usernames, final int amount, final int delay, final boolean ping, final String proxy, final Player sender) {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            try {
                for (int i = 0; i < amount; i++) {
                    final String username = (usernames + i);

                    final Proxy foundProxy = Util.getProxyByName(proxy);
                    final Bot bot = new Bot(username, sender);
                    if (ping) {
                        final ServerPinger pinger = new ServerPinger(sender, false, bot.getConnection());
                        pinger.connect(host, port, foundProxy);
                    } else {
                        bot.connect(host, port, foundProxy);
                    }
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
                sender.sendChatMessage("&aSent all bots!");
            } catch (Exception exc) {
                sender.sendChatMessage("&cError occur while sending bots!");
            }
        });
    }

}
