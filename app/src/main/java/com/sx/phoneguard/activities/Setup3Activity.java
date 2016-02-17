package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;

public class Setup3Activity extends BaseSetupActivity {

    private EditText et_safenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        et_safenumber.setText(sp.getString(MyConstants.SAFEPHONE,""));
    }

    private void initView() {
        setContentView(R.layout.activity_setup3);
        et_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
    }

    @Override
    public void prev() {
        startAndFinishActivity(Setup2Activity.class);
    }

    @Override
    public void next() {
        //获得安全号码并保存到sp中
        String safeNumber = et_safenumber.getText().toString().trim();
        if(TextUtils.isEmpty(safeNumber)){
            ShowToast.show(Setup3Activity.this,"安全号码不能为空");
            return;
        }
        sp.edit().putString(MyConstants.SAFEPHONE,safeNumber).commit();
        ShowToast.show(Setup3Activity.this, "安全号码设置成功");
        startAndFinishActivity(Setup4Activity.class);
    }

}
