/*
 * LightProxy
 * Copyright (C) 2021.  VenixPLL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package pl.venixpll.mc.objects;

import com.darkmagician6.eventapi.EventManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.venixpll.events.BotDisconnectedEvent;
import pl.venixpll.events.PacketReceivedEvent;
import pl.venixpll.mc.connection.BotConnection;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.play.ClientPlayerPosLookPacket;
import pl.venixpll.mc.packet.impl.client.play.ClientPluginMessagePacket;
import pl.venixpll.mc.packet.impl.client.play.ClientSettingsPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerJoinGamePacket;
import pl.venixpll.mc.packet.impl.server.play.ServerPlayerPosLookPacket;
import pl.venixpll.mc.packet.impl.server.play.ServerSpawnPositionPacket;

import java.net.Proxy;

@Data
@RequiredArgsConstructor
public class Bot {

    private final String username;
    private final Player owner;
    private BotConnection connection = new BotConnection(this);
    private int entityId = -1;

    public void connect(final String host, final int port, final Proxy proxy) {
        connection.connect(host, port, proxy);
    }

    public void disconnected() {
        final BotDisconnectedEvent event = new BotDisconnectedEvent(this);
        EventManager.call(event);
        owner.getBots().remove(this);
    }


    public void packetReceived(final Packet packet) {
        final PacketReceivedEvent event = new PacketReceivedEvent(packet, this);
        EventManager.call(event);
        if (!event.isCancelled()) {
            if (packet instanceof ServerJoinGamePacket) {
                this.entityId = ((ServerJoinGamePacket) packet).getEntityId();
                owner.sendChatMessage("&a%s Connected!", username);
                owner.getBots().add(this);
                this.connection.sendPacket(new ClientPluginMessagePacket("MC|Brand", "vanilla".getBytes()));
                this.connection.sendPacket(new ClientSettingsPacket("pl_PL", (byte) 32, (byte) 0, true, (byte) 1));
            } else if (packet instanceof ServerSpawnPositionPacket) {
                //Helps in future movement of bot.
                this.connection.sendPacket(new ClientPlayerPosLookPacket(((ServerSpawnPositionPacket) packet).getPosition(), 160, 90, true));
            } else if (packet instanceof ServerPlayerPosLookPacket) {
                //Helps in future movement of bot.
                final ServerPlayerPosLookPacket p = (ServerPlayerPosLookPacket) packet;
                this.connection.sendPacket(new ClientPlayerPosLookPacket(p.getPos(), p.getYaw(), p.getPitch(), p.isOnGround()));
            }
        }
    }
}
