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
