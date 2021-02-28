package pl.venixpll.mc.packet.registry;


import pl.venixpll.mc.data.network.EnumConnectionState;
import pl.venixpll.mc.data.network.EnumPacketDirection;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.client.login.ClientLoginStartPacket;
import pl.venixpll.mc.packet.impl.client.play.*;
import pl.venixpll.mc.packet.impl.client.status.ClientStatusPingPacket;
import pl.venixpll.mc.packet.impl.client.status.ClientStatusRequestPacket;
import pl.venixpll.mc.packet.impl.handshake.HandshakePacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginDisconnectPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginEncryptionRequestPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSetCompressionPacket;
import pl.venixpll.mc.packet.impl.server.login.ServerLoginSuccessPacket;
import pl.venixpll.mc.packet.impl.server.play.*;
import pl.venixpll.mc.packet.impl.server.status.ServerStatusPongPacket;
import pl.venixpll.mc.packet.impl.server.status.ServerStatusResponsePacket;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class PacketRegistry {

    private static HashMap<Integer, Packet> CLIENT_STATUS = new HashMap<>();
    private static HashMap<Integer, Packet> CLIENT_LOGIN = new HashMap<>();
    private static HashMap<Integer, Packet> CLIENT_PLAY = new HashMap<>();

    private static HashMap<Integer, Packet> SERVER_STATUS = new HashMap<>();
    private static HashMap<Integer, Packet> SERVER_LOGIN = new HashMap<>();
    private static HashMap<Integer, Packet> SERVER_PLAY = new HashMap<>();

    public static void registerPacket(EnumConnectionState connectionState, EnumPacketDirection direction, Packet packet) {
        final int packetId = packet.getPacketID();
        switch (direction) {
            case SERVERBOUND:
                switch (connectionState) {
                    case HANDSHAKE:
                        throw new IllegalArgumentException("Cannot add handshakePacket!");
                    case LOGIN:
                        CLIENT_LOGIN.put(packetId, packet);
                        break;
                    case PLAY:
                        CLIENT_PLAY.put(packetId, packet);
                        break;
                    case STATUS:
                        CLIENT_STATUS.put(packetId, packet);
                        break;
                }
                break;
            case CLIENTBOUND:
                switch (connectionState) {
                    case HANDSHAKE:
                        throw new IllegalArgumentException("Cannot add handshakePacket to CLIENTBOUND!");
                    case LOGIN:
                        SERVER_LOGIN.put(packetId, packet);
                        break;
                    case PLAY:
                        SERVER_PLAY.put(packetId, packet);
                        break;
                    case STATUS:
                        SERVER_STATUS.put(packetId, packet);
                        break;
                }
                break;
        }
    }

    public static void load() {
        //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT //CLIENT
        //STATUS
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.SERVERBOUND, new ClientStatusRequestPacket());
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.SERVERBOUND, new ClientStatusPingPacket());
        //LOGIN
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.SERVERBOUND, new ClientLoginStartPacket());
        //PLAY
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientKeepAlivePacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientChatPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientCreativeInventoryAction());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientEntityActionPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.SERVERBOUND, new ClientPluginMessagePacket());
        //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER //SERVER
        //STATUS
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.CLIENTBOUND, new ServerStatusResponsePacket());
        registerPacket(EnumConnectionState.STATUS, EnumPacketDirection.CLIENTBOUND, new ServerStatusPongPacket());
        //LOGIN
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginDisconnectPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginSetCompressionPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginEncryptionRequestPacket());
        registerPacket(EnumConnectionState.LOGIN, EnumPacketDirection.CLIENTBOUND, new ServerLoginSuccessPacket());
        //PLAY
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerJoinGamePacket());// 0x01
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerKeepAlivePacket());// 0x00
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerDisconnectPacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerTimeUpdatePacket());
        registerPacket(EnumConnectionState.PLAY, EnumPacketDirection.CLIENTBOUND, new ServerChatPacket());
    }


    public static Packet getPacket(EnumConnectionState connectionState, EnumPacketDirection direction, int id) {
        return getNewInstance(getPacketA(connectionState, direction, id));
    }

    private static Packet getPacketA(EnumConnectionState connectionState, EnumPacketDirection direction, int id) {
        switch (direction) {
            case SERVERBOUND:
                switch (connectionState) {
                    case HANDSHAKE:
                        return new HandshakePacket();
                    case LOGIN:
                        return CLIENT_LOGIN.get(id);
                    case PLAY:
                        return CLIENT_PLAY.get(id);
                    case STATUS:
                        return CLIENT_STATUS.get(id);
                }
                break;
            case CLIENTBOUND:
                switch (connectionState) {
                    case LOGIN:
                        return SERVER_LOGIN.get(id);
                    case PLAY:
                        return SERVER_PLAY.get(id);
                    case STATUS:
                        return SERVER_STATUS.get(id);

                }
                break;
        }
        return null;
    }

    private static Packet getNewInstance(final Packet packetIn) {
        if (packetIn == null) return null;
        Class<? extends Packet> packet = packetIn.getClass();
        try {
            Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate packet \"" + packetIn.getPacketID() + ", " + packet.getName() + "\".", e);
        }
    }

}
