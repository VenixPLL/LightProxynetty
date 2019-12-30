package pl.venixpll.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.venixpll.mc.objects.Player;

@AllArgsConstructor
@Data
public class PlayerDisconnectedEvent implements Event {

    private Player player;

}
