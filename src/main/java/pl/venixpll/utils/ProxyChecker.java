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

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ProxyChecker {

    public final ArrayList<Proxy> BEST_PROXIES = new ArrayList<>();
    public final ArrayList<Proxy> MIDRANGE_PROXIES = new ArrayList<>();
    public final ArrayList<Proxy> WORST_PROXIES = new ArrayList<>();

    public ProxyChecker checkArray(final List<Proxy> proxies) {
        WORST_PROXIES.clear();
        MIDRANGE_PROXIES.clear();
        WORST_PROXIES.clear();
        final AtomicInteger index = new AtomicInteger(0);
        IntStream.range(0, 10).forEachOrdered(i -> {
            final ExecutorService checkService = Executors.newSingleThreadExecutor();
            checkService.submit(() -> {
                while (true) {
                    if (index.get() == proxies.size() || index.get() > proxies.size()) {
                        checkService.shutdownNow();
                        break;
                    } else {
                        final Proxy checking = proxies.get(index.getAndAdd(1));
                        final int pCheck = NetUtils.checkProxy(checking, 500);
                        if (pCheck != -1) {
                            if (pCheck < 70) {
                                BEST_PROXIES.add(checking);
                            } else if (pCheck > 70 && pCheck < 180) {
                                MIDRANGE_PROXIES.add(checking);
                            } else if (pCheck > 180) {
                                WORST_PROXIES.add(checking);
                            }
                        }
                    }
                }
            });
        });
        return this;
    }

}
