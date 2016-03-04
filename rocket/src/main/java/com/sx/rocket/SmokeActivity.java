package com.sx.rocket;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class SmokeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke);
        ImageView iv_top = (ImageView) findViewById(R.id.top);
        AlphaAnimation aa = new AlphaAnimation(0,1);//从透明到不透明
        aa.setDuration(1000);

        iv_top.startAnimation(aa);
        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(1000);//一秒之后销毁
                finish();
                super.run();
            }
        }.start();
    }
}
