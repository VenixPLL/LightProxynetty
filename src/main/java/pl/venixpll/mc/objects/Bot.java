package pl.venixpll.mc.objects;

import com.darkmagician6.eventapi.EventManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.events.BotDisconnectedEvent;

@Data
@RequiredArgsConstructor
public class Bot{

    private final String username;
    private final Player owner;
    private Session session;
    private boolean connected = false;
    private int entityId = -1;

    public void disconnected(){
        final BotDisconnectedEvent event = new BotDisconnectedEvent(this);
        EventManager.call(event);
        owner.getBots().remove(this);
    }
}
