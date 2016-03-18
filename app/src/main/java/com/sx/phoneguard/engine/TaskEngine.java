package com.sx.phoneguard.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.sx.phoneguard.domain.TaskBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ad on 2016/3/18.
 * 进程管理引擎
 */
public class TaskEngine {
    /**
     * @param context 上下文
     * @return 总内存大小  以字节为单位
     */
    public static long getTotalMemory(Context context) {
        /*
        android 内存的信息都存储在系统内部的一个文件中(/proc/meminfo),所以只需要使用IO流读取到这个
        文件中的信息就可以得到总内存大小
         */
        long size = 0;
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            //把字节流转换为字符流
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            //读取该文件的第一行就可以得到总内存的大小
            String meminfoStr = br.readLine();
            //拿到第一行数据中的数字信息
            char c = 0;
            StringBuilder sb = new StringBuilder();//线程不安全，但是效率高
            for (int i = 0; i < meminfoStr.length(); i++) {
                c = meminfoStr.charAt(i);
                if (c >= '0' && c <= '9') {
                    sb.append(c);//数字信息存储在容器中
                }
            }

            size = Long.parseLong(sb.toString()) * 1024;//数组的大小，单位为字节
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取可用的内存大小
     *
     * @param context 上下文
     * @return 可用内存大小
     */
    public static long getAvaiMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outinfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outinfo);
        return outinfo.availMem;
    }

    /**
     * @param context 上下文
     * @return 获取所有正在运行中的进程
     */
    public static List<TaskBean> getRunningProcess(Context context) {
        List<TaskBean> datas = new ArrayList<>();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //获取正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //遍历这些进程，封装到TaskBean中
        for (ActivityManager.RunningAppProcessInfo info :
                runningAppProcesses) {
            TaskBean bean = new TaskBean();

            String name = info.processName;//包名
            //需要使用包管理器PackageManager来获取应用程序的名字和图标，及大小。
            try {
                PackageInfo packageInfo = pm.getPackageInfo(name, 0);
                String packName = packageInfo.applicationInfo.loadLabel(pm) + "";//获取app的名字
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);//获取图标
                //通过进程id获取进程所占的内存大小
                Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
                //占用内存的大小
                long size = processMemoryInfo[0].getTotalPrivateDirty() * 1024;

                //判断该进程是系统进程还是用户进程,要使用Flags
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {//用户进程
                    bean.setIsSystem(false);
                } else {
                    bean.setIsSystem(true);//系统进程
                }

                //封装信息到TaskBean中
                bean.setIcon(icon);
                bean.setName(packName);
                bean.setSize(size);
                bean.setPackName(name);
                datas.add(bean);
            } catch (PackageManager.NameNotFoundException e) {
                //没有名字的进程就过滤掉了
                e.printStackTrace();
            }

        }


        return datas;
    }
}
