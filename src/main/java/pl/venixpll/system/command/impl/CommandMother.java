package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;

public class CommandMother extends Command {

    public CommandMother() {
        super(",mother","Bot stuff","");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        sender.setMother(!sender.isMother());
        sender.sendChatMessage("&cMother was set to " + sender.isMother());
    }
}
