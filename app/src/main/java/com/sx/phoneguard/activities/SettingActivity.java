package com.sx.phoneguard.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.service.BlackService;
import com.sx.phoneguard.service.PhoneLocationService;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ServiceUtils;
import com.sx.phoneguard.view.SettingView;

public class SettingActivity extends AppCompatActivity {

    private SettingView sv_update;
    private SettingView sv_black;
    SharedPreferences sp;
    private SettingView sv_black_boot;
    private SettingView sv_phonecall_location;
    private TextView tv_style_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (sp.getBoolean(MyConstants.ISCHECKVERSION, false)) {
            sv_update.setChecked(true);
        } else {
            sv_update.setChecked(false);
        }
        if (ServiceUtils.isRun(SettingActivity.this, "com.sx.phoneguard.service.BlackService")) {
            sv_black.setChecked(true);
        } else {
            sv_black.setChecked(false);
        }

        if (ServiceUtils.isRun(SettingActivity.this, "com.sx.phoneguard.service.PhoneLocationService")) {
            sv_phonecall_location.setChecked(true);
        } else {
            sv_phonecall_location.setChecked(false);
        }
        //设置初始化状态
        sv_black_boot.setChecked(sp.getBoolean(MyConstants.BOOTBLACK, false));
        tv_style_text.setText(items[sp.getInt(MyConstants.STYLE, 0)]);
    }

    private void initEvent() {
        sv_update.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sv_update.setChecked(!sv_update.getChecked());
               /* if(sv_update.getChecked()){
                    sv_update.setChecked(false);
                }else {
                    sv_update.setChecked(true);
                }*/

                //设置自动更新，Splash界面
                if (sv_update.getChecked()) {//为true,表示设置自动更新
                    sp.edit().putBoolean(MyConstants.ISCHECKVERSION, true).apply();
                } else {//不设置自动更新
                    sp.edit().putBoolean(MyConstants.ISCHECKVERSION, false).apply();
                }
            }
        });


        sv_black.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv_black.setChecked(!sv_black.getChecked());
                //启动或关闭黑名单拦截服务
                //1,判断服务是否启动
                if (ServiceUtils.isRun(SettingActivity.this, "com.sx.phoneguard.service.BlackService")) {
                    //服务已经启动，就关闭服务
                    Intent intent = new Intent(SettingActivity.this, BlackService.class);
                    stopService(intent);
                } else {
                    Intent intent = new Intent(SettingActivity.this, BlackService.class);
                    startService(intent);
                }
                //2，创建服务，拦截短信，电话监听
            }
        });
        /**
         * setClickListener:自定义方法设置自组件的事件
         */
        sv_black_boot.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //状态信息需要保存在sp中
                boolean isSet = sp.getBoolean(MyConstants.BOOTBLACK, false);
                isSet = !isSet;
                sp.edit().putBoolean(MyConstants.BOOTBLACK, isSet).apply();
                //需要监听广播，可以开启和关闭，所以需要在服务中注册这个广播接收者
                sv_black_boot.setChecked(isSet);
            }
        });

        sv_phonecall_location.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv_phonecall_location.setChecked(!sv_phonecall_location.getChecked());
                //启动或关闭来电显示归属地
                //1,判断服务是否启动
                if (ServiceUtils.isRun(SettingActivity.this, "com.sx.phoneguard.service.PhoneLocationService")) {
                    //服务已经启动，就关闭服务
                    Intent intent = new Intent(SettingActivity.this, PhoneLocationService.class);
                    stopService(intent);
                } else {
                    Intent intent = new Intent(SettingActivity.this, PhoneLocationService.class);
                    startService(intent);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        sv_update = (SettingView) findViewById(R.id.sv_setting_update);
        sv_black = (SettingView) findViewById(R.id.sv_setting_black);
        sv_black_boot = (SettingView) findViewById(R.id.sv_setting_black_bootcomplete);
        sv_phonecall_location = (SettingView) findViewById(R.id.sv_setting_phonecall_location);
        tv_style_text = (TextView) findViewById(R.id.tv_setting_item_style_text);
    }

    /**
     * 弹出单选对话框选择吐司的提示风格
     *
     * @param view view
     */
    public void selectStyle(View view) {
        //弹出单选对话框
        alertSingleDialog();
    }

    private String[] items = {"金属灰", "苹果绿", "卫士蓝", "活力橙", "半透明"};

    private void alertSingleDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setSingleChoiceItems(items, sp.getInt(MyConstants.STYLE, 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt(MyConstants.STYLE, which).apply();
                dialog.dismiss();
                tv_style_text.setText(items[which]);
            }
        });
        ab.show();
    }
}
