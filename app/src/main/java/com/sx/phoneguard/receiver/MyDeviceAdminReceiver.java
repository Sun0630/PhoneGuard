package com.sx.phoneguard.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 设备管理员，特殊的广播接收者
 */
public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    public MyDeviceAdminReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
