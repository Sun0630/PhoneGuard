package com.sx.phoneguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

public class LockClearService extends Service {
    public LockClearService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //写一个接收锁屏信息的广播接收者
    private class LockCleraReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //取出当前运行的所有进程，然后kill掉
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info :
                    runningAppProcesses) {
                am.killBackgroundProcesses(info.processName);
            }

            System.out.println("锁屏清理完成>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }

    private LockCleraReceiver receiver = new LockCleraReceiver();

    @Override
    public void onCreate() {
        //注册广播
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
