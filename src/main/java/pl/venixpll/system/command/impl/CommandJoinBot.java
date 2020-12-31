package pl.venixpll.system.command.impl;

import pl.venixpll.mc.connection.BotConnection;
import pl.venixpll.mc.connection.ServerPinger;
import pl.venixpll.mc.objects.Bot;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.utils.NetUtils;

import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandJoinBot extends Command {

    public CommandJoinBot() {
        super(",joinbot", "Connecting bots to server", "<host:port> <usernames> <amount> <delay> <ping> <proxies>");
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

        final String usernames = args[2];
        final int amount = Integer.parseInt(args[3]);
        final int delay = Integer.parseInt(args[4]);
        final boolean ping = Boolean.parseBoolean(args[6]);
        connect(sender, delay, host, port, usernames, amount, ping);
    }

    private void connect(final Player sender, final int delay, final String host, final int port, final String usernames, final int amount, final boolean ping) {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            sender.sendChatMessage("&aSending!");
            for (int i = 0; i < amount; i++) {
                final String username = (usernames + i);

                if(ping){
                    final ServerPinger pinger = new ServerPinger(sender,false);
                    pinger.connect(host, port, Proxy.NO_PROXY);
                }
                new BotConnection().connect(host, port, Proxy.NO_PROXY, new Bot(username, sender));
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sender.sendChatMessage("&aSent all bots!");
        });
    }
}
