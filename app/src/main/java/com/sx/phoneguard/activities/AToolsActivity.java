package com.sx.phoneguard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.SmsUtils;

public class AToolsActivity extends AppCompatActivity {

    private ProgressBar pb;
    private ProgressBar pb_resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        pb = (ProgressBar) findViewById(R.id.pb_smsbake_progress);
        pb_resume = (ProgressBar) findViewById(R.id.pb_smsresume_progress);
    }


    /**
     * 手机归属地查询
     *
     * @param view 按钮的点击事件
     */
    public void phoneLocation(View view) {
        Intent intent = new Intent(AToolsActivity.this, PhoneLocationActivity.class);
        startActivity(intent);
    }

    /**
     * 短信还原
     *
     * @param view
     */
    public void smsRecovery(View view) {
        pb_resume.setVisibility(View.VISIBLE);
        final SmsUtils.ProgressCallBack callBack = new SmsUtils.ProgressCallBack() {
            @Override
            public void setProgress(int current) {
                pb_resume.setProgress(current);
            }

            @Override
            public void setMax(int max) {
                pb_resume.setMax(max);
            }
        };
        new Thread() {
            @Override
            public void run() {
                SmsUtils.smsResume(getApplicationContext(), callBack);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb_resume.setVisibility(View.GONE);
                    }
                });

                super.run();
            }
        }.start();
    }

    /**
     * 短信备份
     *
     * @param view view
     */
    public void smsBackup(View view) {
        //备份短信是个耗时操作，要开启子线程
        //设置一个进度条对话框
       /* pb.setVisibility(View.VISIBLE);
        SmsUtils.ProgressCallBack callBack = new SmsUtils.ProgressCallBack() {
        @Override
        public void setProgress(int current) {
            pb.setProgress(current);
        }

        @Override
        public void setMax(int max) {
            pb.setMax(max);
        }
    };
        new Thread() {
            @Override
            public void run() {
                SmsUtils.BakeUpSms(getApplicationContext(), callBack);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                    }
                });

                super.run();
            }
        }.start();*/


        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        final SmsUtils.ProgressCallBack callBack = new SmsUtils.ProgressCallBack() {
            @Override
            public void setProgress(int current) {
                pd.setProgress(current);
            }

            @Override
            public void setMax(int max) {
                pd.setMax(max);
            }
        };

        pd.show();
        new Thread() {
            @Override
            public void run() {
                SmsUtils.BakeUpSms(getApplicationContext(), callBack);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                super.run();
            }
        }.start();
    }

    public void appLock(View view) {
        //启动程序锁界面
        Intent intent = new Intent(this,LockAppFragment.class);
        startActivity(intent);
        finish();
    }

    public void chouti(View view) {
        Intent intent = new Intent(this,ChouTiActivity.class);
        startActivity(intent);
        finish();
    }
}
