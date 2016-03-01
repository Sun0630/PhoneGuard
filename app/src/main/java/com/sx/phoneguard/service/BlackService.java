package com.sx.phoneguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.sx.phoneguard.db.dao.BlackNumberDao;
import com.sx.phoneguard.domain.BlackNumberData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlackService extends Service {


    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyPhoneStateListener listener;

    public BlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 短信黑名单拦截
     */
    public class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信的内容
            Object[] datas = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : datas) {
                //构造短信
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                //消息内容
                String messageBody = smsMessage.getDisplayMessageBody();
                //短信号码
                String number = smsMessage.getDisplayOriginatingAddress();
                System.out.println(number);
                if ((dao.getMode(number) & BlackNumberData.SMS) != 0) {//黑名单号码存在
                    abortBroadcast();
                } else {//短信内容的拦截

                }
            }
        }
    }

    //利用服务来注册广播接收者
    private SmsReceiver smsReceiver = new SmsReceiver();

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //设置优先级
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);
        //拿到电话管理器，用来监听电话状态的改变
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        //设置电话监听的状态
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        dao = new BlackNumberDao(getApplicationContext());
        super.onCreate();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //监听电话状态改变
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    checkPhone(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机，通话状态
                    break;
            }
        }

        private void checkPhone(String incomingNumber) {
            //拿到这个号码的拦截模式
            int mode = dao.getMode(incomingNumber);
            if ((mode & BlackNumberData.PHONE) != 0) {//有电话拦截
                //挂断电话
                seeLog(incomingNumber);
                endCall();
                //挂断电话之后还要把通话记录删掉
                //先删除以前的记录，然后生成新的电话日志
                //                SystemClock.sleep(1000);不科学
                //查看电话日志，通过内容观察者

                //                removeCallLog(incomingNumber);
                System.out.println("挂断电话");
            }
        }

        private void seeLog(final String incomingNumber) {
            Uri uri = Uri.parse("content://call_log/calls");
            //注册内容观察者
            final ContentObserver contentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    removeCallLog(incomingNumber);
                    //取消注册内容观察者，以免删除正常的通话记录
                    getContentResolver().unregisterContentObserver(this);
                    super.onChange(selfChange);
                }
            };
            getContentResolver().registerContentObserver(uri, true, contentObserver);
        }

        private void removeCallLog(String incomingNumber) {
            Uri uri = Uri.parse("content://call_log/calls");
            getContentResolver().delete(uri, " number = ? ", new String[]{incomingNumber});
        }

        private void endCall() {
            //挂断电话，需要使用反射
            try {
                Class<?> type = Class.forName("android.os.ServiceManager");
                Method getServiceMethod = type.getDeclaredMethod("getService", new Class[]{String.class});
                IBinder binder = (IBinder) getServiceMethod.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
                iTelephony.endCall();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        //取消注册短信的广播
        unregisterReceiver(smsReceiver);
        smsReceiver = null;
        //取消电话的监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
}
