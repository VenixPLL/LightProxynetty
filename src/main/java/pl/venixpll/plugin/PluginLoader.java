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

package pl.venixpll.plugin;

import pl.venixpll.LightProxy;
import pl.venixpll.utils.LogUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginLoader {

    public static List<ProxyPlugin> plugins = new ArrayList<>();

    public static void loadPlugins() {
        final File directory = new File("plugins");
        final ClassLoader mainLoader = LightProxy.class.getClassLoader();
        if (directory.exists()) {
            try {
                final File[] files = directory.listFiles();
                for (File f : files) {
                    if (f.getAbsolutePath().endsWith(".jar")) {
                        LogUtil.printMessage("[PLUGIN] Trying to load " + f.getName());
                        final ClassLoader pluginLoader = URLClassLoader.newInstance(new URL[]{f.toURL()}, mainLoader);
                        final Class clazz = pluginLoader.loadClass("Main");
                        if (clazz.getSuperclass().equals(ProxyPlugin.class)) { //Nie wykonuje sie
                            final ProxyPlugin plugin = (ProxyPlugin) clazz.getConstructor().newInstance();
                            plugins.add(plugin);
                            LogUtil.printMessage("[PLUGIN] Loaded " + f.getName());
                        }
                    }
                }
            } catch (Exception exc) {
                LogUtil.printMessage("[FATAL] Error occur while loading plugins!");
            }
        } else {
            LogUtil.printMessage("[FATAL] Plugins folder does not exist!");
            prepareLaunch();
        }
    }

    public static void enablePlugins() {
        plugins.forEach(p -> {
            p.onLoad();
        });
    }

    public static void prepareLaunch() {
        final File file = new File("plugins");
        if (!file.exists()) file.mkdir();
    }

}
