package pl.venixpll.mc.packet.impl.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSuccessPacket;
import pl.venixpll.utils.LogUtil;
import pl.venixpll.utils.WorldUtils;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class NetHandlerLoginServer implements INetHandler {

    private final Player player;

    @Override
    public void disconnected() {
        LogUtil.printMessage("[%s] Disconnected during login sequence!", player.getUsername());
    }

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof ClientLoginStartPacket) {
            player.setUsername(((ClientLoginStartPacket) packet).getUsername());
            player.setCompressionThreshold(256);
            player.sendPacket(new ServerLoginSuccessPacket(UUID.randomUUID(), player.getUsername()));
            LogUtil.printMessage("[%s] Logged in!", player.getUsername());
            player.setConnectionState(EnumConnectionState.PLAY);
            player.setPacketHandler(new NetHandlerPlayServer(player));
            WorldUtils.emptyWorld(player);
            WorldUtils.lobby(player, "Lobby.dat");
            LightProxy.getServer().playerList.add(player);
        }
    }
}
