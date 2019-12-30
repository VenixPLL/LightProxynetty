package pl.venixpll.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.venixpll.mc.objects.Bot;

@Data
@AllArgsConstructor
public class BotConnectedEvent extends EventCancellable implements Event {

    private Bot bot;

}
