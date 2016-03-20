package com.sx.phoneguard.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.sx.phoneguard.R;
import com.sx.phoneguard.engine.TaskEngine;
import com.sx.phoneguard.receiver.LockCleraReceiver;
import com.sx.phoneguard.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

public class AppWidgetService extends Service {

    private AppWidgetManager wm;
    private Timer timer;
    private TimerTask task;
    private LockCleraReceiver receiver;

    public AppWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {

        //注册清理进程的广播
       /* receiver = new LockCleraReceiver();
        IntentFilter filter = new IntentFilter("sx.receiver");
        registerReceiver(receiver, filter);*/

        //通过Widget管理器来设置数据更新
        wm = AppWidgetManager.getInstance(getApplicationContext());

        //写一个定时器，设置每隔多长时间更新一下数据
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //待进行的任务
                //设置更新
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
                //远程View，相对于Android系统
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);

                String runningNumber = "正在运行的软件:" + TaskEngine.getRunningProcess(getApplicationContext()).size();
                views.setTextViewText(R.id.process_count, runningNumber);

                String memInfo = "可用内存" + Formatter.formatFileSize(getApplicationContext(),
                        TaskEngine.getAvaiMem(getApplicationContext()));
                views.setTextViewText(R.id.process_memory, memInfo);

                //添加按钮事件，一键清理
                Intent intent = new Intent();
                intent.setAction("com.sx.aaa");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                wm.updateAppWidget(provider, views);
            }
        };
        timer.schedule(task, 0, 5000);//每隔5秒更新数据
        super.onCreate();
    }



    @Override
    public void onDestroy() {
       /* unregisterReceiver(receiver);
        receiver = null;*/
        timer.cancel();
        timer = null;
        task.cancel();
        task = null;
        super.onDestroy();
    }
}
