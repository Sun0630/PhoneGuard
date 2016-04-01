package com.sx.phoneguard.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.sx.phoneguard.R;
import com.sx.phoneguard.db.dao.VirusDao;
import com.sx.phoneguard.domain.AppBean;
import com.sx.phoneguard.domain.VirusBean;
import com.sx.phoneguard.engine.AppEngine;
import com.sx.phoneguard.utils.Md5Utils;
import com.sx.phoneguard.utils.ShowToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class AntiVirusActivity extends AppCompatActivity {

    public static final int BEGINING = 0;
    public static final int FINISH = 1;
    public static final int CONNECTION = 3;
    private TextView tv_name;
    private ImageView iv_scan;
    private LinearLayout ll_content;
    private ProgressBar pb_progress;
    private boolean isScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        checkVersion();
        //scan();//扫描病毒
    }

    private Dialog dialog;
    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECTION://联网
                    //弹出对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(AntiVirusActivity.this);
                    builder.setTitle("提醒").setMessage("正在尝试联网");
                    dialog = builder.create();
                    dialog.show();
                    break;
                case FINISH://没联网
                    dialog.dismiss();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                /*
               业务需求：
                    1，访问服务器，获取新的版本号
                    2，判断版本是否有变化
                    3，如果没有变化，直接扫描病毒
                    4，如果有变化，提醒用户是否更新
                        1>更新，获取新的病毒信息(json)，更新病毒，更新完，开始扫描
                        2>不更新，直接扫描
                 */
                Message msg = Message.obtain();
                msg.what = CONNECTION;
                handler2.sendMessage(msg);

                //1，访问服务器，获取新的版本号
                HttpUtils utils = new HttpUtils();
                utils.configTimeout(5000);//设置超时
                utils.send(HttpRequest.HttpMethod.GET, getResources().getString(R.string.getVirusVersion),
                        new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Message msg = Message.obtain();
                                msg.what = FINISH;
                                handler2.sendMessage(msg);
                                //判断版本是否有变化
                                System.out.println(responseInfo.result + ">>>>>>>>>>>>>>>>>>>>");
                                //拿到本地病毒库的版本号和服务器端的版本号并比较
                                int myVersion = VirusDao.getVersion(getApplicationContext());
                                int serverVersion = Integer.parseInt(responseInfo.result);
                                if (myVersion != serverVersion) {//表示有新病毒
                                    //弹出对话框提示用户
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AntiVirusActivity.this);
                                    builder.setTitle("提醒").setMessage("发现新的病毒！是否更新病毒库？");
                                    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //更新病毒
                                            HttpUtils utils1 = new HttpUtils();
                                            utils1.send(HttpRequest.HttpMethod.GET, getResources().getString(R.string.getVirues),
                                                    new RequestCallBack<String>() {
                                                        @Override
                                                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                                            //解析json数据，json数据存在responseInfo
                                                            try {
                                                                JSONObject json = new JSONObject(responseInfo.result);
                                                                VirusBean bean = new VirusBean();//把数据封装到bean中
                                                                bean.setDesc(json.getString("desc"));
                                                                bean.setName(json.getString("name"));
                                                                bean.setType(json.getInt("type"));
                                                                bean.setMd5(json.getString("md5"));
                                                                VirusDao.addVirus(AntiVirusActivity.this, bean);//更新一个病毒

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            ShowToast.show(AntiVirusActivity.this,"病毒库更新成功");
                                                            scan();
                                                        }

                                                        @Override
                                                        public void onFailure(HttpException e, String s) {
                                                            ShowToast.show(AntiVirusActivity.this,"病毒库更新失败");
                                                            //scan();
                                                        }
                                                    });
                                        }
                                    });
                                    builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            scan();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    //没有新病毒，继续扫描
                                    scan();
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                //没有网络，继续扫描
                                ShowToast.show(AntiVirusActivity.this, "没有网络");
                                Message msg = Message.obtain();
                                msg.what = FINISH;
                                handler2.sendMessage(msg);
                                scan();
                            }
                        });
                super.run();
            }
        }.start();
    }

    //更新界面，消息队列
    private Handler handler = new Handler() {

        private TextView tv;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BEGINING:
                    Data data = (Data) msg.obj;
                    tv_name.setText("正在扫描 " + data.name);

                   /* tv = new TextView(AntiVirusActivity.this);
                    //如果没有发现病毒，使用黑色字体显示，病毒用红色字体显示
                    if (data.desc == null) {//没有发现病毒
                        tv.setTextColor(Color.BLACK);
                        tv.setText("扫描 " + data.name + "没有发现病毒.");
                    } else {
                        tv.setTextColor(Color.RED);
                        tv.setText(data.name + "发现病毒" + data.desc + "请立即查杀！");
                    }
                    ll_content.addView(tv, 0);*/

                    View v = View.inflate(getApplicationContext(), R.layout.item_antivirus_view, null);
                    ImageView iv = (ImageView) v.findViewById(R.id.iv_anticirus_appicon);
                    iv.setBackgroundDrawable(data.icon);

                    TextView tv = (TextView) v.findViewById(R.id.tv_anticirus_apptitle);
                    tv.setText(data.name);
                    //判断是否有病毒，图标不一样
                    ImageView ok = (ImageView) v.findViewById(R.id.iv_anticirus_ok);
                    if (data.desc != null) {
                        ok.setImageResource(R.drawable.list_icon_risk);
                    }

                    ll_content.addView(v, 0);

                    break;

                case FINISH:
                    Toast.makeText(getApplicationContext(), "扫描完成！", Toast.LENGTH_SHORT).show();
                    //关闭动画
                    iv_scan.clearAnimation();

                    tv_name.setText("扫描完毕！您的手机很安全，没有发现病毒");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //信息的封装类
    private class Data {
        String name;
        String desc;
        Drawable icon;
    }


    private void scan() {
        isScan = true;
        new Thread() {
            @Override
            public void run() {
                //扫描所有的apk
                List<AppBean> allAllApps = AppEngine.getAllAllApps(AntiVirusActivity.this);
                //设置进度条
                pb_progress.setMax(allAllApps.size());
                int number = 0;
                for (AppBean appbean :
                        allAllApps) {
                    if (!isScan) {
                        return;
                    }
                    String path = appbean.getPath();//拿到apk的安装路径
                    String desc = VirusDao.isVirus(getApplicationContext(), new File(path));
                    System.out.println(Md5Utils.getFileMd5(new File(path))+":"+appbean.getName());
                    Data data = new Data();
                    data.desc = desc;
                    data.name = appbean.getName();
                    data.icon = appbean.getIcon();
                    Message msg = Message.obtain();
                    msg.what = BEGINING;
                    msg.obj = data;
                    handler.sendMessage(msg);
                    pb_progress.setProgress(++number);
                    SystemClock.sleep(200);
                }
                //扫描结束，发送消息
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);

                super.run();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_anti_virus);
        tv_name = (TextView) findViewById(R.id.tv_antivirus_name);
        iv_scan = (ImageView) findViewById(R.id.iv_antivirus_scan);
        ll_content = (LinearLayout) findViewById(R.id.ll_antivirus_content);
        pb_progress = (ProgressBar) findViewById(R.id.pb_antivirus_checking);

        //设置扫描旋转动画
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画插入器，设置旋转的速度为匀速
        ra.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 1 * input;
            }
        });
        ra.setDuration(900);
        ra.setRepeatCount(Animation.INFINITE);

        iv_scan.startAnimation(ra);
    }

    @Override
    protected void onDestroy() {
        isScan = false;
        super.onDestroy();
    }
}
