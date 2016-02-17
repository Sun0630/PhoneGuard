package com.sx.phoneguard.activities;

import android.os.Bundle;

import com.sx.phoneguard.R;

public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void prev() {

    }

    @Override
    public void next() {
        startAndFinishActivity(Setup2Activity.class);
    }

}
