package com.sx.phoneguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ad on 2016/2/14.
 */
public class Md5Utils {


    /**
     * @param file 传入一个文件
     * @return 这个文件对应的MD5值
     */
    public static String getFileMd5(File file) {
        StringBuilder res = new StringBuilder("");
        try {
            MessageDigest instance = MessageDigest.getInstance("md5");
            //使用流读取文件
            FileInputStream fis = new FileInputStream(file);
            //时间换空间
            byte[] buffer = new byte[1024];
            int len = fis.read(buffer);

            while (len != -1) {
                instance.update(buffer, 0, len);
                len = fis.read(buffer);
            }
            byte[] digest = instance.digest();
            //遍历这个字节数组，并转换成16进制
            for (byte b :
                    digest) {
                String hexString = Integer.toHexString(b & 0xff);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                res.append(hexString);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res+"";
    }


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
