package com.sx.phoneguard.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.sx.phoneguard.R;
import com.sx.phoneguard.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager mDPM;

    public SmsReceiver() {
    }

    /**
     * 短信拦截的receiver
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //拿到设备管理器对象
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        //接收短信进行处理
        Object[] datas = (Object[]) intent.getExtras().get("pdus");
        for (Object d : datas) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) d);
            String smsBody = smsMessage.getDisplayMessageBody();
            if (smsBody.equals("#*music*#")) {//播放音乐
                System.out.println(smsBody);
                //播放音乐，能重置系统音量为最大,即使静音
                MediaPlayer player = MediaPlayer.create(context, R.raw.qqqg);
                player.setVolume(1,1);
                player.start();
                //停止广播
                abortBroadcast();
            } else if (smsBody.equals("#*GPS*#")) {//定位
                //停止广播
                System.out.println(smsBody);
                //开启定位服务
                Intent intent1 = new Intent(context, LocationService.class);
                context.startService(intent1);
                abortBroadcast();
            } else if (smsBody.equals("#*lockscreen*#")) {//锁屏
                //停止广播
                System.out.println(smsBody);
                mDPM.resetPassword("123", 0);//重置密码
                mDPM.lockNow();//立即锁屏
                abortBroadcast();
            } else if (smsBody.equals("#*wipedata*#")) {//清除数据
                //停止广播
                System.out.println(smsBody);
                mDPM.wipeData(mDPM.WIPE_EXTERNAL_STORAGE);//清除sd卡
                abortBroadcast();
            }
        }
    }
}
