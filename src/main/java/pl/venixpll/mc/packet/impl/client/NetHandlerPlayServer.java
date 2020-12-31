package pl.venixpll.mc.packet.impl.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.LightProxy;
import pl.venixpll.mc.objects.Player;
import pl.venixpll.mc.packet.INetHandler;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.play.ClientChatPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientKeepAlivePacket;
import pl.venixpll.system.command.CommandManager;
import pl.venixpll.utils.LogUtil;

@RequiredArgsConstructor
@Data
public class NetHandlerPlayServer implements INetHandler {

    private final Player player;

    @Override
    public void disconnected() {
        LogUtil.printMessage("[%s] Disconnected.",player.getUsername());
    }

    @Override
    public void handlePacket(Packet packet) {
        if(packet instanceof ClientKeepAlivePacket){
            final int time = ((ClientKeepAlivePacket) packet).getTime();
            player.setPing((int) (System.currentTimeMillis() - time));
        }else if(packet instanceof ClientChatPacket){
            final String message = ((ClientChatPacket) packet).getMessage();
            if(message.startsWith(",")){
                CommandManager.onCommand(message,player);
            } else if (message.startsWith("@")) {
                for (Player p : LightProxy.getServer().getPlayerList()) {
                    p.sendChatMessageNoPrefix("&6" + player.getUsername() + " &8» &e" + message.replace("@", ""));
                }
            } else{
               forwardPacket(packet);
            }
        }else{
            forwardPacket(packet);
        }
    }

    private void forwardPacket(final Packet packet){
        if(player.getConnector() != null && player.getConnector().isConnected()){
            if(player.isMother()){
                player.getBots().forEach(bot -> {
                    if(bot.getConnection().isConnected()) {
                        //TODO add Entity Action exclude;
                        bot.getConnection().sendPacket(packet);
                    }
                });
            }
            player.getConnector().sendPacket(packet);
        }
    }
}
