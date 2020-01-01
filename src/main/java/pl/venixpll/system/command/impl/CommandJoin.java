package pl.venixpll.system.command.impl;

import javafx.scene.effect.Light;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.connection.ServerConnector;
import pl.venixpll.mc.connection.ServerPinger;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.utils.NetUtils;
import pl.venixpll.utils.Util;

import java.net.InetSocketAddress;
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
