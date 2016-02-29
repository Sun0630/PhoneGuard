package com.sx.phoneguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.sx.phoneguard.db.dao.PhoneLocationDao;

public class PhoneLocationService extends Service {


    private TelephonyManager tm;
    private PhoneLocationDao dao;
    private PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    String s = dao.find(getApplicationContext(), incomingNumber);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机
                    break;
                case TelephonyManager.CALL_STATE_IDLE://挂断
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };;

    public PhoneLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        dao = new PhoneLocationDao();

        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
}
