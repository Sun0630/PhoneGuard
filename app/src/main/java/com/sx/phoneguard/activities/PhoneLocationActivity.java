package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.db.dao.PhoneLocationDao;

public class PhoneLocationActivity extends AppCompatActivity {

    private EditText et_number;
    private TextView tv_location;
    private PhoneLocationDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new PhoneLocationDao();
        initView();
        initEvent();
    }

    private void initEvent() {
        /**
         * 为输入框中的文本添加一个监听变化的侦听器
         */
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_phone_location);
        et_number = (EditText) findViewById(R.id.et_phonelocation_phonenumber);
        tv_location = (TextView) findViewById(R.id.tv_phonelocation_location);
    }


    public void search() {
        String number = et_number.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            //抖动效果
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_number.startAnimation(animation);
            //震动效果
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{200, 100, 300, 100, 250, 200}, -1);
            return;
        }
        String location = dao.find(PhoneLocationActivity.this, number);
        tv_location.setText("归属地是:" + location);
    }

    public void search(View view) {
        search();
    }
}
