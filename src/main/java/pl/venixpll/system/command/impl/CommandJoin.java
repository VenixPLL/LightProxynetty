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

import pl.venixpll.mc.connection.ServerConnector;
import pl.venixpll.mc.connection.ServerPinger;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.utils.NetUtils;
import pl.venixpll.utils.Util;

import java.net.Proxy;

public class CommandJoin extends Command {

    public CommandJoin() {
        super(",join", "Connecting to server", "<host:port> <username> <ping> <proxy>");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final String[] args = cmd.split(" ");
        String host = args[1];
        int port = 25565;
        if(host.contains(":")){
            final String[] sp = host.split(":",2);
            host = sp[0];
            port = Integer.parseInt(sp[1]);
        }
        if(NetUtils.checkSocketConnection(host,port,500) == -1){
            final String[] resolved = NetUtils.getServerAddress(host);
            host = resolved[0];
            port = Integer.parseInt(resolved[1]);
            if(NetUtils.checkSocketConnection(host,port,500) == -1){
                sender.sendChatMessage("&cNo connection.");
                return;
            }
        }
        final String username = args[2];
        final boolean ping = Boolean.parseBoolean(args[3]);
        final ServerConnector connector = new ServerConnector(sender,username);
        sender.setConnector(connector);

        final Proxy proxy = Util.getProxyByName(args[4]);

        if(proxy != Proxy.NO_PROXY) {
            if (NetUtils.checkProxy(proxy, 300) == -1) {
                sender.sendChatMessage("&cProxy does not work! try another one! or \"none\"");
                return;
            }
        }

        if(ping){
            final ServerPinger pinger = new ServerPinger(sender,true,connector);
            pinger.connect(host,port, proxy);
        }else{
            connector.connect(host,port,proxy);
        }
    }
}
