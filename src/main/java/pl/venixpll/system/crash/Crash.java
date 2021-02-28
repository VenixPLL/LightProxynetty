package pl.venixpll.system.crash;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.venixpll.mc.objects.Player;

@Data
@AllArgsConstructor
public abstract class Crash {

    private String name;
    private CrashType crashType;

    public abstract void init();

    public abstract void execute(final String message, final Player sender);

}
