package com.sx.rocket;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class RocketsService extends Service {

    private WindowManager wm;
    private View view;
    private WindowManager.LayoutParams params;

    public RocketsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //1,拿到窗体管理器
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //2，自定义吐司的View
        view = View.inflate(getApplicationContext(), R.layout.rockets, null);
        //添加动画效果
        ImageView iv_rocket = (ImageView) view.findViewById(R.id.iv_rocket);
        AnimationDrawable ad = (AnimationDrawable) iv_rocket.getBackground();
        ad.start();

        //3,初始化参数
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  取消不能触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        //类型 TYPE_TOAST土司的类型  天生不相应触摸事件
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.setTitle("Toast");
        //显示吐司
        show();
        //4,增加触摸事件
        initEvent();
        super.onCreate();
    }

    /**
     * 显示吐司
     */
    public void show() {
        wm.addView(view, params);
    }

    /**
     * 是吐司消失
     */
    public void dismiss() {
        if (view != null) {
            wm.removeView(view);
        }
    }

    //使用消息队列来更新吐司的UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                stopSelf();//关闭当前服务
                return;
            }
            wm.updateViewLayout(view, params);
        }
    };

    private void initEvent() {
        view.setOnTouchListener(new View.OnTouchListener() {

            private int downX;
            private int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 按下移动
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int dx = moveX - downX;//dx x方向位置变化
                        int dy = moveY - downY; //dy y方向位置变化
                        params.x += dx;
                        params.y += dy;
                        //更新土司的位置
                        wm.updateViewLayout(view, params);
                        downX = moveX;
                        downY = moveY;

                        break;
                    case MotionEvent.ACTION_UP:// 松开
                        //保存土司的位置
                        if (params.y > 300) {
                            params.x = wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
                            //发射小火箭
                            new Thread() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 68; i++) {
                                        params.y -= 20;
                                        SystemClock.sleep(10);
                                        //发送消息
                                        handler.obtainMessage().sendToTarget();
                                    }

                                    Message msg = Message.obtain();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                    super.run();
                                }

                            }.start();

                            //开启冒烟的Activity
                            Intent intent = new Intent(getApplicationContext(), SmokeActivity.class);
                            //在service中启动Activity需要设置,开启一个新的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        dismiss();//关闭小火箭
        super.onDestroy();
    }
}
