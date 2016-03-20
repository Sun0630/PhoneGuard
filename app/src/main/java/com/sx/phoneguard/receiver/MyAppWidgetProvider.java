package com.sx.phoneguard.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.sx.phoneguard.service.AppWidgetService;

/**
 * Created by ad on 2016/3/20.
 * 桌面小控件
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        //第一次打开Widget的时候调用
        //创建服务，监测数据的变化
        Intent service = new Intent(context, AppWidgetService.class);
        context.startService(service);
        System.out.println("onEnable");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        //删除所有Widget的时候调用
        //关闭服务
        Intent service = new Intent(context, AppWidgetService.class);
        context.stopService(service);
        System.out.println("onEnable");
        super.onDisabled(context);
    }
}
