package com.sx.phoneguard.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.view.SettingView;

public class SettingActivity extends AppCompatActivity {

    private SettingView sv_update;
    private SettingView sv_black;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.SPNAME,MODE_PRIVATE);
        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if(sp.getBoolean(MyConstants.ISCHECKVERSION,false)){
            sv_update.setChecked(true);
        }else {
            sv_update.setChecked(false);
        }
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
                if(sv_update.getChecked()){//为true,表示设置自动更新
                    sp.edit().putBoolean(MyConstants.ISCHECKVERSION,true).commit();
                }else {//不设置自动更新
                    sp.edit().putBoolean(MyConstants.ISCHECKVERSION,false).commit();
                }
            }
        });


        sv_black.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv_black.setChecked(!sv_black.getChecked());

              /*  if (sv_black.getChecked()) {
                    sv_black.setChecked(false);
                } else {
                    sv_black.setChecked(true);
                }*/
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        sv_update = (SettingView) findViewById(R.id.sv_setting_update);
        sv_black = (SettingView) findViewById(R.id.sv_setting_black);

    }
}
