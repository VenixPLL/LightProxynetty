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

package pl.venixpll;

import lombok.Getter;
import pl.venixpll.api.ProxyAPI;
import pl.venixpll.mc.data.chat.Message;
import pl.venixpll.mc.data.status.PlayerInfo;
import pl.venixpll.mc.data.status.ServerStatusInfo;
import pl.venixpll.mc.data.status.VersionInfo;
import pl.venixpll.mc.packet.registry.PacketRegistry;
import pl.venixpll.mc.server.MinecraftServer;
import pl.venixpll.plugin.PluginLoader;
import pl.venixpll.system.LowLevelCommandTask;
import pl.venixpll.system.command.CommandManager;
import pl.venixpll.system.crash.CrashRegistry;
import pl.venixpll.utils.ImageUtil;
import pl.venixpll.utils.LogUtil;
import pl.venixpll.utils.ProxyChecker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LightProxy {

    @Getter
    private static MinecraftServer server;

    public static ProxyChecker PLChecker;
    public static ProxyChecker GLChecker;

    public static void main(String... args) throws Exception {
        LogUtil.setupLogging(null);
        PacketRegistry.load();
        LogUtil.printMessage("Loading plugins...");

        PluginLoader.prepareLaunch();
        PluginLoader.loadPlugins();
        PluginLoader.enablePlugins();

        LogUtil.printMessage("Preparing Proxies...");
        final List<Proxy> PL_Proxies = ProxyAPI.getProxies(ProxyAPI.getURL("socks4","PL"));
        final List<Proxy> GL_Proxies = ProxyAPI.getProxies(ProxyAPI.getURL("socks4",null));
        LogUtil.printMessage("Proxy Status: PL: %s, GL: %s",PL_Proxies.size(),GL_Proxies.size());
        PLChecker = new ProxyChecker().checkArray(PL_Proxies);
        GLChecker = new ProxyChecker().checkArray(GL_Proxies);
        final ExecutorService commandTask = Executors.newSingleThreadExecutor();
        commandTask.submit(new LowLevelCommandTask());
        CommandManager.init();
        CrashRegistry.init();
        server = new MinecraftServer(25565).bind("Server is now running on port %s");

        //Reading status image;
        final File statusFile = new File("server_icon.png");
        BufferedImage bufferedImage = null;
        if(statusFile.exists()) {
            bufferedImage = ImageIO.read(new File("server_icon.png"));
        }
        final VersionInfo versionInfo = new VersionInfo(LogUtil.fixColor("&fLight&6Proxy"),399);
        final PlayerInfo playerInfo = new PlayerInfo(0,0);
        final Message desc = Message.fromString(LogUtil.fixColor("&fLight&6Proxy &8» &6Version &c0.1 &6SHIT\n&fLight&6Proxy &8» &6Some text"));
        server.setStatusInfo(new ServerStatusInfo(versionInfo,playerInfo,desc, statusFile.exists() ? ImageUtil.iconToString(bufferedImage) : null));
    }

}
