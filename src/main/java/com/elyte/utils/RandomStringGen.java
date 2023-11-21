package com.elyte.utils;
import java.util.UUID;
import java.security.SecureRandom;

public class RandomStringGen {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


    static SecureRandom rnd = new SecureRandom();


    public static String generateString() {
        return UUID.randomUUID().toString();
    }


    
    String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
           sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
     }
}
