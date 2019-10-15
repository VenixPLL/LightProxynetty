package pl.venixpll.mc.packet;

import pl.venixpll.mc.objects.Player;

public interface INetHandler {

    void disconnected();
    void handlePacket(final Packet packet);


}
