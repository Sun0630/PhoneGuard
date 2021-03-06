package com.sx.phoneguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.sx.phoneguard.service.BlackService;
import com.sx.phoneguard.utils.MyConstants;

public class BootCompleteReceiver extends BroadcastReceiver {

    private SmsManager smsManager;
    private SharedPreferences sp;
    private TelephonyManager tm;

    public BootCompleteReceiver() {
    }

    /**
     * 当广播接收者收到开机的广播的时候，向安全号码发送一个SIM卡变更的短信
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        fangDao(context);//启动手机防盗的功能

        startBlackService(context);//判断开机是否启动黑名单拦截服务

    }

    private void startBlackService(Context context) {
        //在这里控制广播是否注册
        if(sp.getBoolean(MyConstants.BOOTBLACK, false)){//说明需要开启广播
            //开启服务
            Intent service = new Intent(context,BlackService.class);
            context.startService(service);
        }
    }

    private void fangDao(Context context) {
        smsManager = SmsManager.getDefault();
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sp = context.getSharedPreferences(MyConstants.SPNAME, context.MODE_PRIVATE);

        //得到新SIM信息和就SIM卡信息
        String newSim = tm.getSimSerialNumber();
        String oldSim = sp.getString(MyConstants.SIM,"")+"1";//方便测试，就把旧的SIM序列号加1，表示SIM已经更换过了
        if(newSim.equals(oldSim)){
            //新旧一样表示没有换卡
        }else {
            smsManager.sendTextMessage(sp.getString(MyConstants.SAFEPHONE,"110"),null,"Hello boy,i am the theif,this is" +
                    "my new phonenumber.",null,null);
        }
    }
}
