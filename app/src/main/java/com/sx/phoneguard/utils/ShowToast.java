package com.sx.phoneguard.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by ad on 2016/2/11.
 */
public class ShowToast {
    public static void show(final Activity context, final String str) {
        //判断当前线程是否是主线程，如果是，直接弹出吐司，如果不是，就使用Activity的runOnUiThread方法
        if (Thread.currentThread().equals("main")) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
