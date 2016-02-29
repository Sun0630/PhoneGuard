package com.sx.phoneguard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sx.phoneguard.R;

public class AToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 手机归属地查询
     * @param view
     *  按钮的点击事件
     */
    public void phoneLocation(View view){
        Intent intent = new Intent(AToolsActivity.this,PhoneLocationActivity.class);
        startActivity(intent);
    }
}
