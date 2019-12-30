package pl.venixpll.mc.objects;

import com.darkmagician6.eventapi.EventManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.events.BotDisconnectedEvent;
import pl.venixpll.events.PacketReceivedEvent;
import pl.venixpll.mc.connection.BotConnection;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.server.play.ServerJoinGamePacket;

import java.net.Proxy;

@Data
@RequiredArgsConstructor
public class Bot{

    private final String username;
    private final Player owner;
    private BotConnection connection = new BotConnection(this);

    public void connect(final String host, final int port, final Proxy proxy){
        connection.connect(host,port,proxy);
    }

    public void disconnected(){
        final BotDisconnectedEvent event = new BotDisconnectedEvent(this);
        EventManager.call(event);
        owner.getBots().remove(this);
    }


    public void packetReceived(final Packet packet){
        final PacketReceivedEvent event = new PacketReceivedEvent(packet,this);
        EventManager.call(event);
        if(!event.isCancelled()){
            if(packet instanceof ServerJoinGamePacket){
                owner.sendChatMessage("&a%s Connected!",username);
                owner.getBots().add(this);
            }
        }
    }
}
