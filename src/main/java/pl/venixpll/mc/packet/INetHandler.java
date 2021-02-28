package pl.venixpll.mc.packet;

public interface INetHandler {

    void disconnected();

    void handlePacket(final Packet packet);


}
