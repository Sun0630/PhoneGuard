package com.sx.phoneguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.db.dao.PhoneLocationDao;
import com.sx.phoneguard.utils.MyConstants;

public class PhoneLocationService extends Service {

    private boolean isFirst = true;
    private TelephonyManager tm;
    private PhoneLocationDao dao;
    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    String s = dao.find(getApplicationContext(), incomingNumber);
                    //                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    show(s);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机
                    break;
                case TelephonyManager.CALL_STATE_IDLE://空闲 ---电话管理器只要已注册这个事件就执行了。
                    //只过滤第一次
                    if (!isFirst) {
                        dismissToast();
                    } else {
                        isFirst = false;
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };
    ;
    private OutCallReceiver outCallReceiver;
    private WindowManager wm;
    private View view;
    private WindowManager.LayoutParams params;
    private TextView tv_location;
    private SharedPreferences sp;

    public PhoneLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 初始化吐司
     */
    public void initToast() {
        view = View.inflate(getApplicationContext(), R.layout.sys_toast, null);
        tv_location = (TextView) view.findViewById(R.id.title);

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //TYPE_TOAST：吐司的类型，也是天生不响应触摸事件
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//显示在电话界面上面，优先级高
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;取消不能触摸
        params.gravity = Gravity.TOP | Gravity.LEFT;//设置对齐方式为左上角
        params.x = sp.getInt("X", 0);
        params.y = sp.getInt("Y", 0);

    }

    /**
     * 自定义吐司的显示
     *
     * @param location 电话归属地
     */
    public void show(String location) {
        tv_location.setText(location);
        wm.addView(view, params);
    }

    /**
     * 使自定义吐司消失
     */
    public void dismissToast() {
        if (view != null) {
            wm.removeView(view);
        }
    }

    public void initEvent() {
        /**
         * 为吐司加的onTouch事件
         */
        view.setOnTouchListener(new View.OnTouchListener() {
            private int downX;
            private int downY;
            private int moveX;
            private int moveY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP://松开
                        //保存吐司的位置
                        if (params.x < 0) {
                            params.x = 0;
                        } else if (params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        } else if (params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }

                        sp.edit().putInt("X", params.x).apply();
                        sp.edit().putInt("Y", params.y).apply();
                        break;
                    case MotionEvent.ACTION_DOWN://按下
                        //获取坐标
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://按下并移动
                        //鼠标移动的位置
                        moveX = (int) event.getRawX();
                        moveY = (int) event.getRawY();

                        int dx = moveX - downX;//x坐标轴上的位置变化
                        int dy = moveY - downY;//y坐标轴上的位置变化
                        params.x += dx;
                        params.y += dy;
                        //更新吐司的位置
                        wm.updateViewLayout(view, params);
                        downX = moveX;
                        downY = moveY;
                        break;
                }
                return false;
            }
        });
    }

    private class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到电话号码，查询出归属地，显示至吐司
            String number = getResultData();
            String location = dao.find(context, number);
            //            Toast.makeText(getApplicationContext(), location, Toast.LENGTH_SHORT).show();
            show(location);
        }
    }

    @Override
    public void onCreate() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
        //初始化吐司的组件
        initToast();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        dao = new PhoneLocationDao();
        //初始化电话的监听
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //注册一个外拨电话的广播接收者，当拨出电话的时候也能显示号码的归属地
        outCallReceiver = new OutCallReceiver();
        //初始化吐司的触摸事件
        initEvent();
        registerReceiver(outCallReceiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消电话的监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        //取消外拨电话的广播接收者
        unregisterReceiver(outCallReceiver);
        super.onDestroy();
    }
}
