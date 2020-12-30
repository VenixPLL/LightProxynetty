package pl.venixpll.system.command;

import com.darkmagician6.eventapi.EventManager;
import pl.venixpll.events.PlayerCommandEvent;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.impl.*;
import pl.venixpll.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandManager  {

    public static final List<Command> commands = new ArrayList<>();

    public static void init(){
        commands.add(new CommandHelp());
        commands.add(new CommandJoin());
        commands.add(new CommandQuit());
        commands.add(new CommandNBT());
        commands.add(new CommandJoinBot());
        commands.add(new CommandMother());
        commands.add(new CommandBroadcast());
        commands.add(new CommandTps());
        commands.add(new CommandRespawn());
        commands.add(new CommandDebugInfo());
    }

    public static void registerCommand(final Command command){
        commands.add(command);
    }

    public static void onCommand(final String message, final Player sender){
        final String[] args = message.split(" ");
        final Optional<Command> commandOptional = commands.stream().filter(cmd -> cmd.getPrefix().equalsIgnoreCase(args[0])).findFirst();
        if(commandOptional.isPresent()){
            try {
                final PlayerCommandEvent event = new PlayerCommandEvent(message,sender);
                EventManager.call(event);
                if(!event.isCancelled()) {
                    commandOptional.get().onExecute(message, sender);
                    LogUtil.printMessage("[%s] " + message, sender.getUsername());
                }
            }catch(final Exception exc){
                sender.sendChatMessage("&cCorrect usage&8: &7" + commandOptional.get().getUsage());
            }
        }else{
            sender.sendChatMessage("&cCommand not found!");
        }
    }

}
