package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.view.LockFragment;
import com.sx.phoneguard.view.UnLockFragment;


public class LockAppFragment extends FragmentActivity {

    private TextView tv_unlock;
    private TextView tv_lock;
    private FrameLayout fl_content;


    private View.OnClickListener listener;
    private android.support.v4.app.FragmentManager fm;
    private UnLockFragment unlock;
    private LockFragment lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        //拿到Fragment管理器
        fm = getSupportFragmentManager();
        initData();
    }

    private void initData() {
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_content,unlock);
        ft.commit();
    }

    private void initEvent() {
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.lock){//点击已加锁
                    tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                    System.out.println("显示已加锁的界面");

                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fl_content, lock);
                    ft.commit();
                }else {
                    tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                    System.out.println("显示未加锁的界面");
                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fl_content, unlock);
                    ft.commit();
                }
            }
        };

        tv_lock.setOnClickListener(listener);
        tv_unlock.setOnClickListener(listener);
    }

    private void initView() {
        setContentView(R.layout.activity_lock);
        tv_unlock = (TextView) findViewById(R.id.unlock);
        tv_lock = (TextView) findViewById(R.id.lock);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        unlock = new UnLockFragment();
        lock = new LockFragment();
    }
}
