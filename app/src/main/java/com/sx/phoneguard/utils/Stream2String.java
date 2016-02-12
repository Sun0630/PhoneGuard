package com.sx.phoneguard.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ad on 2016/2/11.
 */
public class Stream2String {
    public static String process(InputStream is) {
        String res = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {

            while ((line = br.readLine()) != null) {
                res += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
