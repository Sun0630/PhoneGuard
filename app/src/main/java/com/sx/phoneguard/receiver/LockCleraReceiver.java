package com.sx.phoneguard.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;



    //写一个接收锁屏信息的广播接收者
    public class LockCleraReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //取出当前运行的所有进程，然后kill掉
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info :
                    runningAppProcesses) {
                am.killBackgroundProcesses(info.processName);
            }

            System.out.println("锁屏清理完成>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }

