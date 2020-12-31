package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;

public class CommandTps extends Command {

    public CommandTps() {
        super(",tps","Checking tick rate of current server!","");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        if(sender.isConnected()){
            sender.sendChatMessage("&6TPS&8: &c" + sender.getLastTps());
        }else{
            sender.sendChatMessage("&cYou have to be connected!");
        }
    }
}
