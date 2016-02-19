package com.sx.phoneguard.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.sx.phoneguard.receiver.SmsReceiver;

public class StartSmsreceiverService extends Service {

    private SmsReceiver receiver;

    public StartSmsreceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //注册监听短信的广播接收者
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,filter);
    }

    @Override
    public void onDestroy() {
        //销毁广播接收者
        unregisterReceiver(receiver);
        receiver = null;
    }
}
