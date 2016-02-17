package com.sx.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sx.lockscreen.receiver.MyDeviceAdminReceiver;

public class RemoveActivity extends AppCompatActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
        //获得设备管理器对象
        mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(RemoveActivity.this, MyDeviceAdminReceiver.class);
        remove();
    }

    /**
     * 一键卸载
     */
    private void remove() {
        //1,取消激活设备管理员
        mDPM.removeActiveAdmin(mDeviceAdminSample);
        /*
		 * <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
		 */

        //2,打开卸载应用的apk
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+getPackageName()));
        startActivity(intent);
    }
}
