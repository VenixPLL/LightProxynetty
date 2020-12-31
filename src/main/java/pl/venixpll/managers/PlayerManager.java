package pl.venixpll.managers;


import io.netty.channel.Channel;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.objects.Session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerManager {
    private static List<Player> players = new CopyOnWriteArrayList<>();

    public static List<Player> getPlayers() {
        return players;
    }

    public static void removePlayer(Player player) { players.remove(player); }

    public static void createPlayer(Player player) { players.add(player); }

    public static void createPlayer(Session session) { players.add(new Player(session)); }

    public static Player getPlayer(Session session) {
        for(Player p : players) {
            if(p.getSession() == session) {
                return  p;
            }
        }
        return null;
    }

    public static Player getPlayer(Channel channel) {
        for(Player p : players) {
            if(p.getSession().getChannel() == channel) {
                return  p;
            }
        }
        return null;
    }
}