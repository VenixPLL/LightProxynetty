package pl.venixpll.utils;

import java.util.Random;

public class Util {

    public static final Random random = new Random();

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String randomString(final int len){
        final StringBuilder sb = new StringBuilder(len);
        for(int i=0;i<len;i++)
            sb.append(AB.charAt(random.nextInt(AB.length())));
        return sb.toString();
    }



}
