package com.sx.phoneguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.sx.phoneguard.domain.AppBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ad on 2016/3/7.
 * 软件管理的引擎类
 */
public class AppEngine {
    /**
     * 获得手机的内部存储
     * @param context
     * 上下文
     * @return
     * 手机的存储值
     */
    public static long getRom(Context context){
        return Environment.getDataDirectory().getFreeSpace();//得到ROM的大小。

    }
    /**
     * 获得手机的外部存储
     * @param context
     * 上下文
     * @return
     * 手机的存储值
     */
    public static long getSd(Context context){
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    /**
     * 获取手机中的所有apk
     * @param context
     * 上下文
     * @return
     * 所有apk的集合
     */
    public static List<AppBean> getAllAllApps(Context context){
        List<AppBean> apps = new ArrayList<>();
        //获取所有的安装包
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo:installedPackages) {
            AppBean bean = new AppBean();
            bean.setName(packageInfo.applicationInfo.loadLabel(pm)+"");//程序的名字
            bean.setIcon(packageInfo.applicationInfo.loadIcon(pm));//程序的图标
            bean.setPackageName(packageInfo.packageName);//程序的包名
            bean.setPath(packageInfo.applicationInfo.sourceDir);//程序源码的路径
           //根据路径构造一个文件，算出程序的大小
            File file = new File(packageInfo.applicationInfo.sourceDir);
            bean.setSize(file.length());
            /**
             * 判断软件是否是系统软件
             * 1,所在目录不同
             *  用户软件在 /data/data
             *  系统软件在 /system
             *
             * 2,可以使用标记
             */
           int flag = packageInfo.applicationInfo.flags;
            if((flag & ApplicationInfo.FLAG_SYSTEM) != 0){//是系统软件
                bean.setIsSystem(true);
            }else {
                bean.setIsSystem(false);
            }

            if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){//在外部存储中
                bean.setIsRom(false);
            }else {
                bean.setIsRom(true);
            }

            apps.add(bean);
        }
        return apps;
    }

}
