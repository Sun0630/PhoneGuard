package com.sx.phoneguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.sx.phoneguard.activities.WatchDogInputPassActivity;
import com.sx.phoneguard.db.LockDB;
import com.sx.phoneguard.db.dao.LockDao;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService extends Service {

    private boolean working;
    private ActivityManager am;
    private LockDao dao;
    private List<String> packs = new ArrayList<>();//把所有的加锁的APP都放在容器中
    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String shurenPackName;

    private class FriendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sx.shuren")) {
                shurenPackName = intent.getStringExtra("packname");
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                working = false;
                //锁屏的时候清理熟人
                shurenPackName = "";
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                startDog();//开启看门狗
            }
        }
    }

    private FriendReceiver receiver;

    @Override
    public void onCreate() {
        //注册内容观察者
        getContentResolver().registerContentObserver(Uri.parse(LockDB.URI), true,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        //重新取数据
                        packs = dao.getLockedPacks();
                        super.onChange(selfChange);
                    }
                });


        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new LockDao(getApplicationContext());
        packs = dao.getLockedPacks();
        //注册广播
        receiver = new FriendReceiver();
        IntentFilter filter = new IntentFilter("com.sx.shuren");

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(receiver, filter);

        //启动看门狗监控
        startDog();
        super.onCreate();
    }

    private void startDog() {
        new Thread() {
            @Override
            public void run() {
                working = true;
                while (working) {
                    //监控每一个新打开的app
                    List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                    //最新打开的APP任务栈
                    ActivityManager.RunningTaskInfo info = infos.get(0);
                    //获取到最新打开的APP的包名
                    String pn = info.topActivity.getPackageName();
                    //判断该APP是否是加锁的
                    //if (dao.isLocked(pn)) {//从硬盘判断。加锁了
                    if (packs.contains(pn)){//从内存判断，提高了效率
                        if (pn.equals(shurenPackName)) {
                            //休息
                        } else {
                            //打开输入密码的界面
                            Intent intent = new Intent(getApplicationContext(), WatchDogInputPassActivity.class);
                            //在服务中打开Activity中需要配置这个属性,放到自己的任务栈，不是重新开启的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent.putExtra("packname", pn);
                            startActivity(intent);
                        }
                    } else {
                        //正常打开界面
                    }
                    SystemClock.sleep(500);
                }
                super.run();
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        working = false;
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
