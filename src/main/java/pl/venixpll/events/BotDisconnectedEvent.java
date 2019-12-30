package pl.venixpll.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.venixpll.mc.objects.Bot;

@AllArgsConstructor
@Data
public class BotDisconnectedEvent implements Event {

    private Bot bot;

}
