package com.sx.phoneguard.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sx.phoneguard.R;
import com.sx.phoneguard.service.LockClearService;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ServiceUtils;

public class TaskManagerSettingActivity extends AppCompatActivity {

    private CheckBox cb_isSystem;
    private CheckBox cb_lockClear;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        //设置初始化
        cb_isSystem.setChecked(sp.getBoolean(MyConstants.SHOWSYSTEM, false));
        cb_lockClear.setChecked(ServiceUtils.isRun(getApplicationContext(),"com.sx.phoneguard.service.LockClearService"));
    }

    private void initEvent() {
        //是否显示系统进程
        cb_isSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(MyConstants.SHOWSYSTEM,isChecked).apply();
            }
        });
        //是否锁屏进行进程清理
        cb_lockClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //启动服务，注册锁屏广播，服务关闭，取消注册
                Intent service = new Intent(TaskManagerSettingActivity.this, LockClearService.class);
                if (isChecked){//锁频清理进程
                    startService(service);
                }else {
                    stopService(service);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_task_manager_setting);
        cb_isSystem = (CheckBox) findViewById(R.id.cb_task_setting_issystem);
        cb_lockClear = (CheckBox) findViewById(R.id.cb_task_setting_lockclear);
        sp = getSharedPreferences(MyConstants.SPNAME,MODE_PRIVATE);
    }
}
