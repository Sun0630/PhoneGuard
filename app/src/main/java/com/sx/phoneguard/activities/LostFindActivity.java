package com.sx.phoneguard.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;

public class LostFindActivity extends AppCompatActivity {


    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.ISSET,MODE_PRIVATE);
        //判断有没有设置过向导，如果设置过了，直接进入手机防盗界面，如果没有设置过，需要进行四步的设置向导步骤
        if(sp.getBoolean(MyConstants.ISSET,false)) {//true设置向导完成。
            initView();//初始化界面
        }else {//进入第一个设置向导界面
             //显示第一个向导界面
            startSetUp1();
        }
    }

    private void startSetUp1() {
        Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
        startActivity(intent);
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        //使用SharePreference来保存是否设置过向导的数据

    }
}
