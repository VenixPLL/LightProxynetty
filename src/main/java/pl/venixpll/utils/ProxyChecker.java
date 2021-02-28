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
