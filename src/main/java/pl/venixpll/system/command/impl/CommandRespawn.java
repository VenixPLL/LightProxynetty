package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.client.play.ClientStatusPacket;
import pl.venixpll.system.command.Command;

public class CommandRespawn extends Command {

    public CommandRespawn() {
        super(",respawn","Respawning all bots!","");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        if(!sender.getBots().isEmpty()){
            sender.getBots().forEach(b -> {
                if(b.getConnection().isConnected()) b.getConnection().sendPacket(new ClientStatusPacket(0));
            });
            sender.sendChatMessage("&aRespawned!");
        }else{
            sender.sendChatMessage("&cYou do not have any bots!");
        }
    }
}
