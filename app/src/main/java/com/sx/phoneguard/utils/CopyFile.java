package com.sx.phoneguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ad on 2016/2/29.
 * 拷贝文件的三种实现方式
 *      通过io流
 *      通过文件名
 *      通过路径名
 */
public class CopyFile {
    public static void copy(InputStream is,OutputStream os){
        //用时间换空间
        try {
            byte[] b = new byte[1024];
            int len = is.read(b);
            while (len != -1){
                os.write(b,0,len);
                len = is.read(b);
            }
            //关闭流
            is.close();
            os.close();
        }catch (Exception e){

        }
    }

    public static void copy(File file1,File file2){
        try {
            copy(new FileInputStream(file1),new FileOutputStream(file2));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String path1,String path2){
        copy(new File(path1),new File(path2));
    }


}
