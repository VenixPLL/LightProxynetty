package pl.venixpll.mc.objects;

import com.darkmagician6.eventapi.EventManager;
import lombok.Data;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.venixpll.LightProxy;
import pl.venixpll.events.PacketReceivedEvent;
import pl.venixpll.mc.data.chat.MessagePosition;
import pl.venixpll.mc.data.game.TitleAction;
import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.NetHandlerLoginServer;
import pl.venixpll.mc.packet.impl.client.NetHandlerStatusServer;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.play.ServerChatPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerTitlePacket;

import java.util.ArrayList;
import java.util.List;


@Data
public class Player {
    private final Session session;
    private Session remoteSession;
    private String username;
    private int ping;
    private boolean connected = false;
    private boolean mother;
    private boolean debugInfo;
    private double lastTps;

    private List<Bot> bots = new ArrayList<>();

    public void resetTitle(){
        session.sendPacket(new ServerTitlePacket(TitleAction.RESET));
    }

    public void sendTitle(final String header,final String footer){
        this.sendTitle(header,footer,10,10,10);
    }

    public void sendTitle(final String header,final String footer,final int fadeIn,final int stay,final int fadeOut){
        if(header != null) session.sendPacket(new ServerTitlePacket(TitleAction.TITLE,header));
        if(footer != null) session.sendPacket(new ServerTitlePacket(TitleAction.SUBTITLE,footer));
        session.sendPacket(new ServerTitlePacket(TitleAction.TIMES,fadeIn,stay,fadeOut));
    }

    public void sendHotbar(final String message, final Object... obj){
        session.sendPacket(new ServerChatPacket(String.format(message,obj), MessagePosition.HOTBAR));
    }

    public void sendChatMessage(final String message,final Object... args){
        session.sendPacket(new ServerChatPacket("&fLight&6Proxy &8» &6" + String.format(message,args)));
    }

    public void sendChatMessageNoPrefix(final String message, final Object... args) {
        session.sendPacket(new ServerChatPacket(String.format(message,args)));
    }

    public void sendHoverMessage(String s1, String s2) {
        TextComponent msg = new TextComponent(s1);
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(s2)));
        session.sendPacket(new ServerChatPacket(msg));
    }

    public void packetReceived(final Packet packet) {
        final PacketReceivedEvent event = new PacketReceivedEvent(packet,this);
        EventManager.call(event);
        if(!event.isCancelled()) {
            if (packet instanceof HandshakePacket) {
                final HandshakePacket handshake = (HandshakePacket) packet;
                session.setProtocolID(handshake.getProtocolId());
                switch (handshake.getNextState()) {
                    case 1:
                        session.setConnectionState(EnumConnectionState.STATUS);
                        session.setPacketHandler(new NetHandlerStatusServer(this));
                        break;
                    case 2:
                        session.setConnectionState(EnumConnectionState.LOGIN);
                        session.setPacketHandler(new NetHandlerLoginServer(this));
                        break;
                }
                if (session.getConnectionState() == EnumConnectionState.HANDSHAKE) {
                    session.getChannel().close();
                }
            } else {
                session.getPacketHandler().handlePacket(packet);
            }
        }
    }

    public void disconnected(){
        if(isConnected()){
            this.remoteSession.getChannel().close();
        }
        LightProxy.getServer().playerList.remove(this);
        if(session.getPacketHandler() != null)
            session.getPacketHandler().disconnected();
    }

    public boolean isConnected() {
        return remoteSession != null && connected;
    }
}
