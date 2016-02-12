package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sx.phoneguard.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_home);
    }
}
