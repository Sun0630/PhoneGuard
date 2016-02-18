package com.sx.phoneguard.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.sx.phoneguard.utils.MyConstants;

public class LocationService extends Service {

    private SharedPreferences sp;
    private LocationManager lm;
    private LocationListener listener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //获得定位管理器
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
        listener = new LocationListener() {
            //位置变化时回调该方法
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();//精确度
                double latitude = location.getLatitude();//纬度
                double longitude = location.getLongitude();//经度
                double altitude = location.getAltitude();//海拔

                String res = "accuracy" + accuracy + "\n";
                res += "latitude" + latitude + "\n";
                res += "longitude" + longitude + "\n";
                res += "altitude" + altitude + "\n";

                //获得位置信息之后，向安全号码发送短信
                SmsManager smsManager = SmsManager.getDefault();
                String safeNumber = sp.getString(MyConstants.SAFEPHONE, "");
                smsManager.sendTextMessage(safeNumber, null, res, null, null);
                stopSelf();//发完信息之后就停止服务
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates("gps", 0, 0, listener);

    }

    /**
     * 取消对位置的监听
     */
    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(listener);
        listener = null;
    }
}
