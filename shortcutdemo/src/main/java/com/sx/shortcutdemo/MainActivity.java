package com.sx.shortcutdemo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createshortcut(View view) {
        Intent intent = new Intent();
        /**
         * 发送一个广播就可以创建快捷方式
         * 创建快捷方式的三要素
         *  1,长什么样   图标
         *  2,做什么事
         *  3,叫什么名
         */

        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //长什么样
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //叫什么名
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"emergency");
        //做什么事
        Intent doWhat = new Intent(Intent.ACTION_CALL);
        //设置只创建一个快捷方式
        intent.putExtra("duplicate",false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,doWhat);
        doWhat.setData(Uri.parse("tel:110"));
        sendBroadcast(intent);

    }
}
