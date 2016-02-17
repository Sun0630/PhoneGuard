package com.sx.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sx.lockscreen.receiver.MyDeviceAdminReceiver;

public class MainActivity extends AppCompatActivity {


    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeviceAdminSample = new ComponentName(MainActivity.this, MyDeviceAdminReceiver.class);
        //获得设备管理器对象
        mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        if(mDPM.isAdminActive(mDeviceAdminSample)) {//如果已经激活了
            mDPM.lockNow();//一键锁屏
            finish();
        }else {
            activation();
        }
    }


    /**
     * 直接跳转到系统设置--》安全--》设备管理器--》你的apk的激活页面
     */
    public void activation(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "请先激活一键锁屏");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }
}
