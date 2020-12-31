package pl.venixpll.mc.packet.impl.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSetCompressionPacket;
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
        LogUtil.printMessage("[%s] Disconnected during login sequence!",player.getUsername());
    }

    @Override
    public void handlePacket(Packet packet) {
        if(packet instanceof ClientLoginStartPacket){
            player.setUsername(((ClientLoginStartPacket) packet).getUsername());
            player.getSession().sendPacket(new ServerLoginSetCompressionPacket(256));
            player.getSession().setCompressionThreshold(256);
            player.getSession().sendPacket(new ServerLoginSuccessPacket(UUID.randomUUID(), player.getUsername()));
            System.out.println("protocol: " + player.getSession().getProtocolID());
            LogUtil.printMessage("[%s] Logged in!",player.getUsername());
            player.getSession().setConnectionState(EnumConnectionState.PLAY);
            player.getSession().setPacketHandler(new NetHandlerPlayServer(player));
            WorldUtils.emptyWorld(player);
            LightProxy.getServer().playerList.add(player);
        }
    }
}
