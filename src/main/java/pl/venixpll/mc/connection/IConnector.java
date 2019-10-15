package pl.venixpll.mc.connection;

import java.net.Proxy;

public interface IConnector {

    void connect(final String host,final int port,final Proxy proxy);

}
