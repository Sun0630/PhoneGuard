package com.sx.phoneguard.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;

public class LostFindActivity extends AppCompatActivity {


    private SharedPreferences sp;
    private TextView tv_safenumber;
    private ImageView iv_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.SPNAME,MODE_PRIVATE);
        //判断有没有设置过向导，如果设置过了，直接进入手机防盗界面，如果没有设置过，需要进行四步的设置向导步骤
        if(sp.getBoolean(MyConstants.ISSET,false)) {//true设置向导完成。
            initView();//初始化界面
            initData();
        }else {//进入第一个设置向导界面
             //显示第一个向导界面
            startSetUp1();
        }
    }

    /**
     * 重新进入设置向导
     */

    public void entersetup(View view){
        startSetUp1();
    }
    private void initData() {
        tv_safenumber.setText(sp.getString(MyConstants.SAFEPHONE,""));
    }

    private void startSetUp1() {
        Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        //使用SharePreference来保存是否设置过向导的数据
        //拿到两个组件
        tv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
        iv_lock = (ImageView) findViewById(R.id.iv_lostfind_lock);
        if(sp.getBoolean(MyConstants.ISLOCK,false)){
            iv_lock.setImageResource(R.drawable.lock);
        }else {
            iv_lock.setImageResource(R.drawable.unlock);
        }
    }
}
