package pl.venixpll.mc.packet.impl.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.status.ClientStatusPingPacket;
import pl.venixpll.mc.packet.impl.client.status.ClientStatusRequestPacket;
import pl.venixpll.mc.packet.impl.server.status.ServerStatusPongPacket;
import pl.venixpll.mc.packet.impl.server.status.ServerStatusResponsePacket;

@RequiredArgsConstructor
@Data
public class NetHandlerStatusServer implements INetHandler {

    private final Player owner;

    @Override
    public void disconnected() {
    }

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof ClientStatusRequestPacket) {
            owner.sendPacket(new ServerStatusResponsePacket(LightProxy.getServer().getStatusInfo()));
            owner.getChannel().close();
        } else if (packet instanceof ClientStatusPingPacket) {
            owner.sendPacket(new ServerStatusPongPacket(((ClientStatusPingPacket) packet).getTime()));
            owner.getChannel().close();
        }
    }
}
