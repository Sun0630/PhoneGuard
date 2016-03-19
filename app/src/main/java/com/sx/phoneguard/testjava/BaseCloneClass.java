package com.sx.phoneguard.testjava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ad on 2016/3/19.
 * 实现克隆技术
 */
public class BaseCloneClass implements Serializable,Cloneable{
    /**
     * CopyOnWriteArrayList
     * 在对其实例进行修改操作（add/remove等）会新建一个数据并修改，
     * 修改完毕之后，再将原来的引用指向新的数组。这样，修改过程没有修改原来的数组。
     */

    /**
     * 原理：先把一个对象从内存中使用对象读取流读取出来，然后在写入内存，这样即生成了这个对象的两个拷贝。
     *
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {

        try {
            /**
             * 1，使用对象输出流把本对象从内存中读取出来
             */
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(output);
            oos.writeObject(this);//把当前对象读取出来
            oos.flush();
            //把字节数组输出流转换成字节数组
            byte[] bytes = output.toByteArray();

            /**
             * 2，再把这个对象写入内存，拷贝一份
             */

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return super.clone();
    }
}
