package com.sx.phoneguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.domain.UrlData;
import com.sx.phoneguard.utils.ShowToast;
import com.sx.phoneguard.utils.Stream2String;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 加载Splash界面
 */
public class SplashActivity extends Activity {

    private static final int UPDATEVERSION = 1;
    private static final int LOADMAIN = 2;
    private TextView tv_title;
    private PackageManager pm;
    private PackageInfo packageInfo;
    private RelativeLayout rl_root;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化界面组件的数据
        initAnimation();//初始化界面的动画效果
        startSubVersion();//访问网络监测版本信息
    }
    //访问网络检测版本信息是个耗时操作，所以要开一个子线程进行
    public void startSubVersion(){
        new Thread(){
            @Override
            public void run() {
                checkVersion();
            }
        }.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATEVERSION:
                    break;
                case LOADMAIN:
                    loadMain();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 访问网络，监测版本信息。URL地址需要配置成静态的，在Values文件夹下进行配置
     */
    private void checkVersion() {
        Message msg = handler.obtainMessage();//此时handler已经和msg绑定
        //让Splash界面休眠一段时间，需要先获得加载数据的时间，然后进行判断
        long startTime = System.currentTimeMillis();//耗时操作的起始时间

        try {
            //建立URL链接
            URL url = new URL(getResources().getString(R.string.url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置初始化参数
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //拿到响应码
            int code = conn.getResponseCode();
            if(code==200){
                //请求成功
                //拿到服务器返回的输入流，也就是json数据，转换成字符串信息
                InputStream is = conn.getInputStream();
                String text = Stream2String.process(is);
                //弹出吐司。因为弹吐司是刷新UI，所以需要在主线程中运行，再次封装一个工具类。
                //解析json数据,
                UrlData urlData = ParseJson(text);
                //处理数据，判断版本号是否一致，如果不一致，说明有新版本
                if(versionCode != urlData.getVersionCode()){
                    //使用对话框显示是否更新新版本
                    msg.what=UPDATEVERSION;

                }else {
                    //没有新版本，直接进入主界面,执行后面finally中的进入主界面

                }
            }else {
                //2000 请求失败，直接进入主界面
                ShowToast.show(SplashActivity.this,"2000 请求失败");

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            //2001 Url错误，直接进入主界面
            ShowToast.show(SplashActivity.this,"2001 Url错误");
        } catch (IOException e) {
            e.printStackTrace();
            //2002 IO错误，网络超时，直接进入主界面
            ShowToast.show(SplashActivity.this,"2002 IO错误，网络超时");
        } catch (JSONException e) {
            e.printStackTrace();
            //2003 json数据格式错误，直接进入主界面
            ShowToast.show(SplashActivity.this,"2003 json数据格式错误");
        }finally {
            if(msg.what==UPDATEVERSION){
                //什么都不做
            }else {
                msg.what = LOADMAIN;
            }
            long endTime = System.currentTimeMillis();//耗时操作的结束时间
            //耗时操作的时间 = endTime - startTime
            //判断时间如果小于2秒，就休眠两秒，如果大于两秒，那么就该几秒几秒
            if((endTime-startTime)<2000){
                //休眠
                SystemClock.setCurrentTimeMillis(2000-(endTime-startTime));
            }else {
                //休眠时间超过2秒。不管了。
            }
            msg.sendToTarget();
        }
    }

    /**
     * 加载进入主界面
     */
    public void loadMain(){
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();//关闭Splash界面
    }

    /**
     * 解析json数据
     * @param text
     */
    private UrlData ParseJson(String text) throws JSONException {
        //封装一个Json数据类，用来存储解析出来的数据
        UrlData data = new UrlData();
        JSONObject json = new JSONObject(text);
        data.setVersionCode(json.getInt("versionCode"));
        data.setDesc(json.getString("desc"));
        data.setDownloadUrl("downloadUrl");
        return data;
    }

    /**
     * 初始化界面的动画效果
     */
    private void initAnimation() {
        //透明渐变动画效果
        AlphaAnimation aa = new AlphaAnimation(0,1);
        aa.setDuration(2000);//设置间隔时间为2秒
        aa.setFillAfter(true);//动画完成之后填充原来的效果

        //设置哪个组件显示动画
        rl_root.setAnimation(aa);
    }

    /***
     * 初始化界面组件的数据
     */
    private void initData() {

        try {
            //拿到当前的包名
            String packageName = getPackageName();
            //包中所有的信息都存储在这个对象中
            packageInfo = pm.getPackageInfo(packageName, 0);
            //拿到版本号和版本名
            versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            //显示到界面上
            tv_title.setText(versionName+":"+ versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        tv_title = (TextView) findViewById(R.id.tv_splash_versionname);
        //从清单文件中获取应用版本号和版本名称，需要拿到包管理器
        pm = getPackageManager();
        //获取相对布局组件
        rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
    }
}
