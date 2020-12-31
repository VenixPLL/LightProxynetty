package pl.venixpll.system.command.impl;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.impl.server.play.ServerChatPacket;
import pl.venixpll.system.command.Command;
import pl.venixpll.system.command.CommandManager;
import pl.venixpll.utils.LogUtil;

import java.util.Comparator;
import java.util.List;

public class CommandHelp extends Command {

    public CommandHelp() {
        super(",help", "Command Help", "");
    }

    @Override
    public void onExecute(String cmd, Player sender) throws Exception {
//        CommandManager.commands.forEach(command -> {
//            sender.sendChatMessage("&f%s &7%s &8- &6%s", command.getPrefix(), command.getUsage(), command.getDesc());
//        });

        List<Command> list = CommandManager.commands;

        list.sort(Comparator.comparingInt(s -> s.getPrefix().length()));

        String[] args = cmd.split(" ");
        int i = 1;
        if(args.length == 2) {
            i = Integer.parseInt(args[1]);
        }

        int k = i - 1;
        int l = Math.min((k + 1) * 10, list.size());
        int j = (list.size() - 1) / 10;

        if(!(i > 0)) {
            sender.sendChatMessage("&cPodana liczba (" + i + ") jest zbyt mała, musi wynosić minimum 1", sender, false);
            return;
        }

        if(i > (j + 1)) {
            sender.sendChatMessage("&cPodana liczba (" + (k + 1) + ") jest zbyt wysoka, nie może przekorczyć " + (j + 1), sender, false);
            return;
        }

        sender.sendChatMessageNoPrefix(" &8--- Wyświetlana strona pomocy: &6" + (k + 1) + " &8z &6" + (j + 1) + " &8---", sender, false);

        for (int i1 = k * 10; i1 < l; ++i1) {
            final Command command = list.get(i1);
            sender.sendHoverMessage("&8>> &f" + command.getPrefix() + " &7" + command.getUsage(), "&6" + command.getDesc());
        }

        TextComponent msg = new TextComponent(LogUtil.fixColor((i != 1) ? "&c[POPRZEDNIA]": ""));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(LogUtil.fixColor("&eKliknij aby przejsc na poprzednia strone!"))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ",help " + (i - 1)));

        TextComponent msg1 = new TextComponent(LogUtil.fixColor((k + 1) != (j + 1) ? "&a[NASTEPNA]" : ""));
        msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(LogUtil.fixColor("&eKliknij aby przejsc na nastepna strone!"))));
        msg1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ",help " + (i + 1)));
        sender.getSession().sendPacket(new ServerChatPacket(msg, new TextComponent(" "), msg1));
    }
}
