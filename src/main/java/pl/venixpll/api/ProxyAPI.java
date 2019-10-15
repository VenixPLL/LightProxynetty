package pl.venixpll.api;

import javax.net.ssl.HttpsURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ProxyAPI {

    public static final String API_URL_FULL = "https://www.proxy-list.download/api/v1/get?type=%s&country=%s";
    public static final String API_URL = "https://www.proxy-list.download/api/v1/get?type=%s";

    public static URL getURL(String type, String country) throws Exception{
        if(country != null){
            return new URL(String.format(API_URL_FULL,type,country));
        }else{
            return new URL(String.format(API_URL,type));
        }
    }

    public static ArrayList<Proxy> getProxies(URL url) throws Exception {
        final ArrayList<Proxy> proxies = new ArrayList<>();

        final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        final Scanner scanner = new Scanner(conn.getInputStream());
        while (scanner.hasNext()) {
            final String[] split = scanner.nextLine().split(":", 2);
            proxies.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(split[0], Integer.parseInt(split[1]))));
        }
        scanner.close();
        return proxies;
    }

}
