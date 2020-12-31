package pl.venixpll.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.venixpll.mc.packet.Packet;

@Data
@AllArgsConstructor
public class PacketReceivedEvent extends EventCancellable implements Event {

    private Packet packet;
    private Object receiver;

}
