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

package pl.venixpll.utils;

import pl.venixpll.LightProxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Random;

public class Util {

    public static final Random random = new Random();

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String randomString(final int len) {
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(random.nextInt(AB.length())));
        return sb.toString();
    }

    public static Proxy getProxyByName(final String pString) {
        if (pString.equalsIgnoreCase("none")) {
            return Proxy.NO_PROXY;
        } else if (pString.equalsIgnoreCase("PL")) {
            return LightProxy.PLChecker.BEST_PROXIES.get(Util.random.nextInt(LightProxy.PLChecker.BEST_PROXIES.size()));
        } else if (pString.equalsIgnoreCase("GL")) {
            return LightProxy.GLChecker.BEST_PROXIES.get(Util.random.nextInt(LightProxy.GLChecker.BEST_PROXIES.size()));
        } else {
            if (pString.contains(":")) {
                final String[] sp = pString.split(":", 2);
                return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(sp[0], Integer.parseInt(sp[1])));
            }
        }
        return Proxy.NO_PROXY;
    }


}
