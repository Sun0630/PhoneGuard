package com.sx.phoneguard.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.app.ActivityManager.RunningServiceInfo;
import java.util.List;

/**
 * Created by ad on 2016/2/22.
 * 判断服务是否在运行
 */

public class ServiceUtils {
    /**
     *
     * @param context
     *      上下文
     * @param serviceType
     *      service的类名
     * @return
     *      是否
     */

    public static boolean isRun(Context context,Class serviceType){
        boolean res = false;
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServices = am.getRunningServices(50);
        for (RunningServiceInfo info:runningServices){
            if (info.service.getClass() == serviceType){
                res = true;
                break;
            }
        }
        return res;
    }
}
