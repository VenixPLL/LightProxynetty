package pl.venixpll.system.command.impl;

import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;

public class CommandDebugInfo extends Command {
    public CommandDebugInfo() {
        super(",debuginfo","Display last packet received from server.","");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
        sender.setDebugInfo(!sender.isDebugInfo());
        sender.sendChatMessage("&6Displaying last packet %s",sender.isDebugInfo() ? "&aYES" : "&cNO");
    }
}
