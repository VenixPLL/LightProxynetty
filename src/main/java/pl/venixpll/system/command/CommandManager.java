package pl.venixpll.system.command;

import com.darkmagician6.eventapi.EventManager;
import org.reflections.Reflections;
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
        new Reflections("pl.venixpll.system.command.impl.*").getSubTypesOf(Command.class).forEach(command -> {
            try {
                commands.add(command.newInstance());
            } catch (Exception ignored) {
            }
        });
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
