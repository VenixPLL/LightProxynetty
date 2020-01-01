package pl.venixpll.plugin.interfaces;

import com.darkmagician6.eventapi.EventTarget;
import pl.venixpll.events.PacketReceivedEvent;
import pl.venixpll.events.PlayerCommandEvent;
import pl.venixpll.events.PlayerConnectedEvent;
import pl.venixpll.events.PlayerDisconnectedEvent;

public interface PlayerListener {

    @EventTarget
    void onConnect(final PlayerConnectedEvent event);

    @EventTarget
    void onPacketReceived(final PacketReceivedEvent event);

    @EventTarget
    void onCommand(final PlayerCommandEvent event);

    @EventTarget
    void onDisconnect(final PlayerDisconnectedEvent event);

}
