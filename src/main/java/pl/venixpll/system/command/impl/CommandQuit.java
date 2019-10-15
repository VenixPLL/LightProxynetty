package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;

public class CommandQuit extends Command {

    public CommandQuit() {
        super(",q", "Disconnecting from server", "");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        if(sender.getConnector() != null && sender.getConnector().isConnected()){
            sender.sendChatMessage("&cDisconnected!");
            sender.getConnector().close();
        }else if(sender.getConnector() != null && !sender.getConnector().isConnected()){
            sender.sendChatMessage("&cYou are not connected!");
        }else if(sender.getConnector() == null){
            sender.sendChatMessage("&cYou are not connected!");
        }
    }
}
