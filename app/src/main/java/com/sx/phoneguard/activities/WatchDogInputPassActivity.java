package com.sx.phoneguard.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sx.phoneguard.R;

/**
 * 看门狗输入密码的界面
 */
public class WatchDogInputPassActivity extends AppCompatActivity {

    private EditText et_pass;
    private String packname;//要加锁的包名
    private ImageView iv_icon;
    private MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packname = getIntent().getStringExtra("packname");
        initView();
        initData();
        //初始化广播接收者
        receiver = new MyReceiver();
        //注册广播接收者
        registerReceiver(receiver,new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    //监听Home键的事件的广播
    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
                //处理home键的事件
                System.out.println(intent.getStringExtra("reason")+">>>>>>>>>>>>>>>>>>>>>>");
            }
        }
    }


    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packname, 0);
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            iv_icon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //按返回键，直接到主界面
        /**
         *
         *   <intent-filter>
         <action android:name="android.intent.action.MAIN" />
         <category android:name="android.intent.category.HOME" />
         <category android:name="android.intent.category.DEFAULT" />
         <category android:name="android.intent.category.MONKEY"/>
         </intent-filter>
         */
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        super.onBackPressed();
    }

    private void initView() {
        setContentView(R.layout.activity_watch_dog_input_pass);
        et_pass = (EditText) findViewById(R.id.et_watchdog_pass);
        iv_icon = (ImageView) findViewById(R.id.iv_watchdog_icon);
    }

    public void submit(View view) {
        String pass = et_pass.getText().toString().trim();
        if (pass.equals("123")) {
            //通知看门狗，这个是熟人，发送广播
            Intent intent = new Intent();
            intent.setAction("com.sx.shuren");
            //通过广播把包名发送出去
            intent.putExtra("packname", packname);
            sendBroadcast(intent);
            //关闭自己，即输入密码的页面
            finish();
        } else {
            //提醒密码不正确

        }
    }

    public void cancle(View view) {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
