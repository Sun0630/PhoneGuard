package com.sx.widgetdemo;

import android.appwidget.AppWidgetProvider;
import android.content.Context;

public class MyAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //创建服务，检查进程信息
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //关闭服务
    }


}
