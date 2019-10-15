package pl.venixpll.utils;

import pl.venixpll.mc.data.Position;
import pl.venixpll.mc.data.game.Difficulty;
import pl.venixpll.mc.data.game.Dimension;
import pl.venixpll.mc.data.game.Gamemode;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.server.play.*;

public class WorldUtils {

    public static void emptyWorld(Player player){
        dimSwitch(player,new ServerJoinGamePacket(0, Gamemode.SURVIVAL, Dimension.OVERWORLD, Difficulty.PEACEFULL,1,"default_1_1",false));
        player.sendPacket(new ServerSpawnPositionPacket(new Position(0,0,0)));
        player.sendPacket(new ServerPlayerAbilitiesPacket(false,true,false,false,2,2));
        player.sendPacket(new ServerPlayerPosLookPacket(new Position(0,0,0),180,90,true));
    }

    public static void dimSwitch(Player player, ServerJoinGamePacket packet){
        player.sendPacket(new ServerRespawnPacket(Dimension.END, Difficulty.PEACEFULL,Gamemode.ADVENTURE,"default_1_1"));
        player.sendPacket(packet);
        player.sendPacket(new ServerRespawnPacket(packet.getDimension(),packet.getDifficulty(),packet.getGamemode(),packet.getLevelType()));
    }
}
