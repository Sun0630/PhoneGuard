package com.sx.phoneguard.root;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Created by ad on 2016/4/4.
 * 一个应用只能对应一个唯一的Application
 */
public class MyApplication extends Application {

    /**
     * 在所有数据生成之前执行
     */
    @Override
    public void onCreate() {
        /**
         * 如果出现异常，没有try catch,异常会抛给jvm，终止程序，(死之前说句话)
         * 不能阻挡改程序挂掉，只是做了一个临时补救的措施
         */
        //获取到当前报出异常的线程
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                System.out.println("这个异常漏掉了吧。。。");

                //获取手机的型号和配置
                //动态获取手机配置
                //通过反射,获取到Build类的字段，因为它里面都是静态方法
                StringWriter out = new StringWriter();
                Field[] declaredFields = Build.class.getDeclaredFields();
                for (Field field :
                        declaredFields) {
                    try {
                        out.write(field.getName() + ":" + field.get(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                PrintWriter pw = new PrintWriter(out);
                ex.printStackTrace(pw);
                try {
                    File dir = new File("/sdcard/phoneguard");
                    if (!dir.exists()){
                        //创建
                        dir.mkdir();
                    }

                    FileOutputStream fos = new FileOutputStream(new File("/sdcard/phoneguard/error.txt"));
                    fos.write(out.toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //处理异常，把异常保存到sd卡的文件中，如果有服务器，可以把文件上传
               /* PrintWriter err = null;
                try {
                    File dir = new File("/sdcard/phoneguard");
                    if (!dir.exists()){
                        //创建
                        dir.mkdir();
                    }

                    err = new PrintWriter(new File("/sdcard/phoneguard/error.txt"));
                    ex.printStackTrace(err);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    if (err != null) {
                        err.close();
                    }
                }*/


                //复活,根据包名打开一个Activity
                PackageManager pm = getPackageManager();
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(getPackageName());

                startActivity(launchIntentForPackage);

                //早死，根据程序的pid杀死这个程序
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
