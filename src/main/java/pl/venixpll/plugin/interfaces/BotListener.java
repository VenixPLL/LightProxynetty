package pl.venixpll.plugin.interfaces;

import com.darkmagician6.eventapi.EventTarget;
import pl.venixpll.events.BotConnectedEvent;
import pl.venixpll.events.BotDisconnectedEvent;
import pl.venixpll.events.PacketReceivedEvent;

public interface BotListener {

    @EventTarget
    void onConnect(final BotConnectedEvent event);

    @EventTarget
    void onPacketReceived(final PacketReceivedEvent event);

    @EventTarget
    void onDisconnect(final BotDisconnectedEvent event);

}
