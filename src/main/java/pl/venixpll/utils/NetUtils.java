package pl.venixpll.utils;


import pl.venixpll.mc.packet.Packet;
import pl.venixpll.mc.packet.impl.CustomPacket;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Hashtable;

public class NetUtils {

    public static int checkSocketConnection(String host,int port,int timeout){
        try {
            final Socket socket = new Socket();
            final long sTime = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return (int)(System.currentTimeMillis() - sTime);
        }catch(Exception e){
            return -1;
        }
    }

    public static int checkProxy(Proxy proxy, int timeout){
        try {
            final Socket socket = new Socket();
            final long sTime = System.currentTimeMillis();
            socket.connect(proxy.address(), timeout);
            socket.close();
            return (int)(System.currentTimeMillis() - sTime);
        }catch(Exception e){
            return -1;
        }
    }

    public static String[] getServerAddress(final String p_78863_0_)
    {
        try {
            final Hashtable var2 = new Hashtable();
            var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            var2.put("java.naming.provider.url", "dns:");
            var2.put("com.sun.jndi.dns.timeout.retries", "1");
            final InitialDirContext var3 = new InitialDirContext(var2);
            final Attributes var4 = var3.getAttributes("_minecraft._tcp." + p_78863_0_, new String[] {"SRV"});
            final String[] var5 = var4.get("srv").get().toString().split(" ", 4);
            return new String[] {var5[3], var5[2]};
        } catch (final Throwable var6) {
            return new String[] {p_78863_0_, Integer.toString(25565)};
        }
    }

    public static Packet createPacket(final int id, final byte[] data){
        return new CustomPacket(id, data);
    }

}
