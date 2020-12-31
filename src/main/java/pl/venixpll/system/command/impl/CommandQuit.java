package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;

public class CommandQuit extends Command {

    public CommandQuit() {
        super(",q", "Disconnecting from server", "<bots(op)>");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final String[] args = cmd.split(" ");
        if(args.length > 0){
            if(args[1].equalsIgnoreCase("bots")){
                if(sender.getBots().isEmpty()){
                    sender.sendChatMessage("&cYou do not have any bots!");
                }else{
                    sender.getBots().forEach(bot -> {
                        bot.getSession().getChannel().close();
                    });
                    sender.sendChatMessage("&cDisconnected all bots!");
                }
                return;
            }
        }
        if(sender.isConnected()){
            sender.sendChatMessage("&cDisconnected!");
            sender.setConnected(false);
            sender.getRemoteSession().getChannel().close();
        } else if(!sender.isConnected()){
            sender.sendChatMessage("&cYou are not connected!");
        }
    }
}
