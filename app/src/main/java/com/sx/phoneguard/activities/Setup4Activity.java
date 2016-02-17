package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_islock;
    private TextView tv_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        cb_islock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //根据状态保存信息
                sp.edit().putBoolean(MyConstants.ISLOCK,isChecked).commit();
                if(isChecked){
                    tv_start.setText("防盗保护开启");
                }else {
                    tv_start.setText("防盗保护已关闭");
                }

            }
        });
    }

    private void initData() {
        if(sp.getBoolean(MyConstants.ISLOCK,false)){//开启
            cb_islock.setChecked(true);
            tv_start.setText("防盗保护已开启");
        }else {
            cb_islock.setChecked(false);
            tv_start.setText("防盗保护已关闭");
        }
    }

    private void initView() {
        setContentView(R.layout.activity_setup4);
        cb_islock = (CheckBox) findViewById(R.id.cb_setup4_islock);
        tv_start = (TextView) findViewById(R.id.tv_setup4_start);
    }

    @Override
    public void prev() {
        startAndFinishActivity(Setup3Activity.class);
    }

    @Override
    public void next() {
        if(!sp.getBoolean(MyConstants.ISLOCK,false)){
            ShowToast.show(Setup4Activity.this,"请确认开启防盗保护");
            return;
        }
        sp.edit().putBoolean(MyConstants.ISSET,true).commit();
        startAndFinishActivity(LostFindActivity.class);
    }
}
