package pl.venixpll.system.crash;

import com.darkmagician6.eventapi.EventManager;
import pl.venixpll.events.PlayerCommandEvent;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.system.command.Command;
import pl.venixpll.system.command.CommandManager;
import pl.venixpll.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrashRegistry {

    private static List<Crash> crashList = new ArrayList<>();

    public static void init(){
        CommandManager.registerCommand(new Command(",crash","Zzzz","<method,list>") {
            @Override
            public void onExecute(String cmd, Player sender) throws Exception {
                if(sender.getConnector().isConnected()){
                    execute(cmd,sender);
                }else{
                    sender.sendChatMessage("&cYou have to be connected!");
                }
            }
        });
        crashList.forEach(c -> c.init());
        LogUtil.printMessage("Loaded %s crashers!",crashList.size());
    }

    public static void registerCrash(final Crash crash,boolean init){
        if(init) crash.init();
        crashList.add(crash);
    }

    public static void execute(final String message, final Player sender){
        final String[] args = message.split(" ");
        if(args[1].equalsIgnoreCase("list")){
            crashList.forEach(c -> sender.sendChatMessage("&c,crash %s <amount> &8- &6%s",c.getName(),c.getCrashType().name()));
        }else {
            final Optional<Crash> crashOptional = crashList.stream().filter(c -> c.getName().equalsIgnoreCase(args[1])).findFirst();
            if (crashOptional.isPresent()) {
                try {
                    crashOptional.get().execute(message, sender);
                } catch (final Exception exc) {
                    sender.sendChatMessage("&cError during crashing current server!");
                }
            } else {
                sender.sendChatMessage("&cCrash method not found!");
                sender.sendChatMessage("&cUse \"crash list\" for list of methods!");
            }
        }
    }

}
