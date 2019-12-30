package pl.venixpll.mc.bot;

import pl.venixpll.LightProxy;
import pl.venixpll.mc.connection.ServerPinger;
import pl.venixpll.mc.objects.Bot;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.utils.Util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BotManager {

    public static void connectSome(final String host, final int port, final String usernames, final int amount, final int delay, final boolean ping, final String proxy, final Player sender){
        final ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            try {
                for (int i = 0; i < amount; i++) {
                    final String username = (usernames + i);

                    Proxy foundProxy;
                    final String pString = proxy;
                    if(pString.equalsIgnoreCase("none")){
                        foundProxy = Proxy.NO_PROXY;
                    }else if(pString.equalsIgnoreCase("PL")){
                        foundProxy = LightProxy.PLChecker.BEST_PROXIES.get(Util.random.nextInt(LightProxy.PLChecker.BEST_PROXIES.size()));
                    }else if(pString.equalsIgnoreCase("GL")){
                        foundProxy = LightProxy.GLChecker.BEST_PROXIES.get(Util.random.nextInt(LightProxy.GLChecker.BEST_PROXIES.size()));
                    }else{
                        if(pString.contains(":")){
                            final String[] sp = pString.split(":",2);
                            foundProxy = new Proxy(Proxy.Type.SOCKS,new InetSocketAddress(sp[0],Integer.parseInt(sp[1])));
                        }else{
                            throw new Exception();
                        }
                    }
                    final Bot bot = new Bot(username,sender);
                    if(ping){
                        final ServerPinger pinger = new ServerPinger(sender,false,bot.getConnection());
                        pinger.connect(host,port, foundProxy);
                    }else {
                        bot.connect(host, port, foundProxy);
                    }
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
                sender.sendChatMessage("&aSent all bots!");
            }catch(Exception exc){
                sender.sendChatMessage("&cError occur while sending bots!");
            }
        });
    }

}
