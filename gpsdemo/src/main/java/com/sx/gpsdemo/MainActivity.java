package com.sx.gpsdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationManager lm;
    private TextView tv_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        gps();
    }

    private void gps() {
        //获得定位管理器
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = lm.getAllProviders();
        for (String s : providers) {
            System.out.println(s);// gps   passive
        }
        LocationListener listener = new LocationListener() {
            //位置变化时回调该方法
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();//精确度
                double latitude = location.getLatitude();//纬度
                double longitude = location.getLongitude();//经度
                double altitude = location.getAltitude();//海拔

                String res = "精确度" + accuracy + "\n";
                res += "纬度" + latitude + "\n";
                res += "经度" + longitude + "\n";
                res += "海拔" + altitude + "\n";

                tv_msg.setText(res);
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
}
