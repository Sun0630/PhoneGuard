package com.sx.rocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*ImageView iv_rockets = (ImageView) findViewById(R.id.im_rocket);
        AnimationDrawable ad = (AnimationDrawable) iv_rockets.getBackground();
        ad.start();*/

    }

    public void start(View view) {
        Intent service = new Intent(this,RocketsService.class);
        startService(service);
        finish();
    }

    public void stop(View view) {
        Intent service = new Intent(this,RocketsService.class);
        stopService(service);
    }
}
