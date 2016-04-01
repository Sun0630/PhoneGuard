package com.sx.phoneguard.testjava;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by ad on 2016/3/29.
 * 获取文件的MD5
 */
public class TestMd5 {

    public static void main(String args[]) {
        String fileMd5 = getFileMd5(new File("实验指导书.pdf"));
        System.out.println(fileMd5);
    }

    /**
     * @param file 传入一个文件
     * @return 这个文件对应的MD5值
     */
    public static String getFileMd5(File file) {
        String res = "";
        try {
            MessageDigest instance = MessageDigest.getInstance("md5");
            //使用流读取文件
            FileInputStream fis = new FileInputStream(file);
            //时间换空间
            byte[] buffer = new byte[1024];
            int len = fis.read(buffer);

            while (len != -1) {
                instance.update(buffer,0,len);
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
                res += hexString;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
