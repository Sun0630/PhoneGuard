package com.sx.phoneguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ad on 2016/2/14.
 */
public class Md5Utils {

    public static String md5JiaMi(String source) {
        String res = "";

        try {
            MessageDigest md5 = MessageDigest.getInstance("Md5");
            byte[] digest = md5.digest(source.getBytes());
            for (byte b : digest) {
                int data = b & 0xff;
                String hex = Integer.toHexString(data);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                res += hex;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return res;
    }
}
