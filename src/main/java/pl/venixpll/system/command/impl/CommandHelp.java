package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.system.command.CommandManager;

public class CommandHelp extends Command {

    public CommandHelp() {
        super(",help", "Command Help", "");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        CommandManager.commands.forEach(command -> {
            sender.sendChatMessage("&f%s &7%s &8- &6%s", command.getPrefix(), command.getUsage(), command.getDesc());
        });
    }
}
