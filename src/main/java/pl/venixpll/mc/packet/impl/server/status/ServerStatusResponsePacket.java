package pl.venixpll.mc.packet.impl.server.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.data.status.PlayerInfo;
import pl.venixpll.mc.data.status.ServerStatusInfo;
import pl.venixpll.mc.data.status.VersionInfo;
import pl.venixpll.mc.objects.GameProfile;
import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.PacketBuffer;

import java.util.Arrays;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ServerStatusResponsePacket extends Packet {

    {
        this.setPacketID(0x00);
    }

    private static final Gson gson = new GsonBuilder().create();

    private ServerStatusInfo statusInfo;

    @Override
    public void write(PacketBuffer packetBuffer) {
        final JsonObject jsonObject = new JsonObject();
        final JsonObject version = new JsonObject();

        String versioName = statusInfo.getVersion().getName();
        version.addProperty("name",versioName);
        version.addProperty("protocol", statusInfo.getVersion().getProtocol());
        final JsonObject players = new JsonObject();
        players.addProperty("max", statusInfo.getPlayers().getMaxPlayers());
        players.addProperty("online", statusInfo.getPlayers().getOnlinePlayers());
        if (statusInfo.getPlayers().getPlayers().length > 0) {
            final JsonArray array = new JsonArray();
            Arrays.stream(statusInfo.getPlayers().getPlayers()).forEach(gameProfile -> {
                final JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("name", gameProfile.getUsername());
                jsonObject1.addProperty("id", gameProfile.getUuid().toString());
                array.add(jsonObject1);
            });
            players.add("sample", array);
        }
        jsonObject.add("version", version);
        jsonObject.add("players", players);
        jsonObject.add("description", statusInfo.getDescription().toJson());
        if (statusInfo.getIcon() != null) {
            jsonObject.addProperty("favicon", statusInfo.getIcon());
        }

        packetBuffer.writeString(jsonObject.toString());
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        final JsonObject jsonObject = gson.fromJson(packetBuffer.readStringFromBuffer(32767), JsonObject.class);
        final JsonObject version = jsonObject.get("version").getAsJsonObject();
        final VersionInfo versionInfo = new VersionInfo(version.get("name").getAsString(), version.get("protocol").getAsInt());
        final JsonObject players = jsonObject.get("players").getAsJsonObject();

        GameProfile[] gameProfiles = new GameProfile[0];
        if (players.has("sample")) {
            final JsonArray profiles = players.get("sample").getAsJsonArray();
            if (profiles.size() > 0) {
                gameProfiles = new GameProfile[profiles.size()];
                for (int index = 0; index < profiles.size(); index++) {
                    final JsonObject jsonObject1 = profiles.get(index).getAsJsonObject();
                    gameProfiles[index] = new GameProfile(jsonObject1.get("name").getAsString(),UUID.fromString(jsonObject1.get("id").getAsString()));
                }
            }
        }

        final PlayerInfo playerInfo = new PlayerInfo(players.get("online").getAsInt(), players.get("max").getAsInt(), gameProfiles);
        final Message description = Message.fromJson(jsonObject.get("description"));
        String icon = null;
        if (jsonObject.has("favicon")) {
            icon = jsonObject.get("favicon").getAsString();
        }

        statusInfo = new ServerStatusInfo(versionInfo, playerInfo, description, icon);
    }
}
