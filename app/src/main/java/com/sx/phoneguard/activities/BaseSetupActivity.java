package com.sx.phoneguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;

public abstract class BaseSetupActivity extends Activity {
    protected SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        sp = getSharedPreferences(MyConstants.SPNAME,MODE_PRIVATE);
    }

    public abstract void prev();
    public abstract void next();

    public void prev(View view){

        prev();
        //位置动画效果的切换
//        overridePendingTransition(R.drawable.prev_out,R.drawable.prev_in);
    }
    public void next(View view){
        //动画效果
        next();
//       overridePendingTransition(R.drawable.next_out,R.drawable.next_in);
    }

    public void startAndFinishActivity(Class type){
        Intent intent = new Intent(BaseSetupActivity.this,type);
        startActivity(intent);
        finish();
    }
}
