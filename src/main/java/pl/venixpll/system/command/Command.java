package pl.venixpll.system.command;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.mc.objects.Player;

@RequiredArgsConstructor
@Data
public abstract class Command {

    private final String prefix;
    private final String desc;
    private final String usage;

    public abstract void onExecute(final String cmd, final Player sender) throws Exception;

}
