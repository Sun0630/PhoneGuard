package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;

public class Setup2Activity extends BaseSetupActivity {

    private ImageView iv_lock;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        if (isBind()) {
            //初始化一个图片
            iv_lock.setImageResource(R.drawable.lock);
        } else {
            iv_lock.setImageResource(R.drawable.unlock);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_setup2);
        iv_lock = (ImageView) findViewById(R.id.iv_setup2_lock);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    //绑定解绑SIM卡
    public void bindSim(View view) {
        //判断SIM卡是否绑定
        if (isBind()) {
            //已经绑定，进行解绑操作
            sp.edit().putString(MyConstants.SIM, "").commit();
            iv_lock.setImageResource(R.drawable.unlock);

        } else {
            //没有绑定，进行绑定,获取SIM信息
            String simSerialNumber = tm.getSimSerialNumber();
            //保存到sp中
            sp.edit().putString(MyConstants.SIM, simSerialNumber).commit();
            iv_lock.setImageResource(R.drawable.lock);
        }

    }

    /**
     * 判断SIM是否绑定
     */
    private boolean isBind() {
        String sim = sp.getString(MyConstants.SIM, "");
        if (TextUtils.isEmpty(sim)) {//没有绑定
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void prev() {
        startAndFinishActivity(Setup1Activity.class);
    }

    @Override
    public void next() {

        if (!isBind()) {
            ShowToast.show(Setup2Activity.this, "请绑定Sim卡");
            return;
        }
        startAndFinishActivity(Setup3Activity.class);
    }

}
