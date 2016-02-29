package com.sx.phoneguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;

public abstract class BaseSetupActivity extends Activity {
    protected SharedPreferences sp;
    private GestureDetector gd;//手势事件的处理，左右滑动Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
        gd = new GestureDetector(BaseSetupActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //e1：起始的滑动点   e2:滑动的过程中记录当前点的坐标，   velocityX：速度
                if (Math.abs(velocityX) > 100) {
                    if (e1.getX() - e2.getX() > 100) {//从左往右滑动
                        next(null);
                    } else if (e1.getX() - e2.getX() < -100){//从右往左滑动
                        prev(null);
                    }
                }else {
                    ShowToast.show(BaseSetupActivity.this,"滑动速度太慢了");
                }
                return true;
            }
        });
    }

    /**
     * 手势和触摸事件绑定
     * @param event
     * 事件
     * @return
     * boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void prev();

    public abstract void next();

    public void prev(View view) {

        prev();
        //位置动画效果的切换
        overridePendingTransition(R.anim.prev_out,R.anim.prev_in);
    }

    public void next(View view) {
        //动画效果
        next();
        overridePendingTransition(R.anim.next_out,R.anim.next_in);
    }

    public void startAndFinishActivity(Class type) {
        Intent intent = new Intent(BaseSetupActivity.this, type);
        startActivity(intent);
        finish();
    }
}
