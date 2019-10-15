package pl.venixpll.mc.data.status;

import lombok.Data;
import pl.venixpll.mc.data.chat.Message;

@Data
public class ServerStatusInfo
{
    private VersionInfo version;
    private PlayerInfo players;
    private Message description;
    private String icon;

    public ServerStatusInfo(final VersionInfo version, final PlayerInfo players, final Message description, final String icon) {
        this.version = version;
        this.players = players;
        this.description = description;
        this.icon = icon;
    }
}
