package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.client.play.ClientChatPacket;
import pl.venixpll.system.command.Command;

public class CommandBroadcast extends Command {

    public CommandBroadcast() {
        super(",bc", "Bot stuff", "<message>");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        final String message = cmd.split(",bc ",2)[1];
        if(sender.getBots().isEmpty()){
            sender.sendChatMessage("&cYou do not have any bots!");
        }else{
            sender.getBots().forEach(bot -> {
                if(bot.isConnected()){
                    bot.getSession().sendPacket(new ClientChatPacket(message));
                }
            });
            sender.sendChatMessage("&cSent message!");
        }
    }
}
