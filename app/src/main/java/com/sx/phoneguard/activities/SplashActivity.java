package com.sx.phoneguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sx.phoneguard.R;
import com.sx.phoneguard.domain.UrlData;
import com.sx.phoneguard.utils.CopyFile;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;
import com.sx.phoneguard.utils.Stream2String;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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
    private ProgressBar pb_download;
    private HttpURLConnection conn;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
        initView();//初始化界面
        initData();//初始化界面组件的数据
        initAnimation();//初始化界面的动画效果
        //当数据库文件存在的时候就不用再次拷贝了
        //创建桌面快捷方式
        createshortcut();
        if(!isExists("address.db")){
            copyThread("address.db");
        }

        if(!isExists("antivirus.db")){
            copyThread("antivirus.db");
        }


        //当设置中心的自动检查更新打钩的时候再检查更新版本。否则直接跳主界面
        if (sp.getBoolean(MyConstants.ISCHECKVERSION, false)) {
            startSubVersion();//访问网络监测版本信息
        } else {
            loadMain();
        }
    }
    private void createshortcut() {
        Intent intent = new Intent();
        /**
         * 发送一个广播就可以创建快捷方式
         * 创建快捷方式的三要素
         *  1,长什么样   图标
         *  2,做什么事
         *  3,叫什么名
         */

        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //长什么样
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //叫什么名
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "361手机卫士");
        //做什么事
        Intent doWhat = new Intent();
        doWhat.setAction("com.sx.home");
        //设置只创建一个快捷方式
        intent.putExtra("duplicate",false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,doWhat);
        sendBroadcast(intent);

    }
        private boolean isExists(String dbName){
            boolean res = false;
            File file = new File("data/data/com.sx.phoneguard/files/" + dbName);
            res = file.exists();
            return res;
        }

    private void copyThread(final String dbName) {
        //拷贝数据库是个耗时的操作，所以需要封装子线程
        new Thread(){
            @Override
            public void run() {
                copyDB(dbName);
            }
        }.start();
    }

    private void copyDB(String dbname) {
        //读取assets目录下的数据库资源，拷贝到工程数据库目录中，因为assets里面的资源不能引用
        AssetManager assets = getAssets();
        try {
            InputStream is = assets.open(dbname);
            //需要把这个数据库文件拷贝到data/data/com.sx.phoneguard/databases/xx.db
            FileOutputStream os = openFileOutput(dbname, MODE_PRIVATE);
            CopyFile.copy(is, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //访问网络检测版本信息是个耗时操作，所以要开一个子线程进行
    public void startSubVersion() {
        new Thread() {
            @Override
            public void run() {
                checkVersion();
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATEVERSION:
                    //弹出对话框提示有新版本，并提供下载新版本的Url
                    //获取新版本的信息
                    final UrlData data = (UrlData) msg.obj;
                    //弹出对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

                    /**
                     * 如果用户不更新也不取消，而是点击了返回键
                     *      1,另返回键无效
                     *      2，设置返回事件
                     */
                    //点返回键无效
                    //                    builder.setCancelable(false);
                    //设置取消返回事件
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //直接加载主界面
                            loadMain();
                        }
                    });

                    builder.setTitle("监测到新版本！").setMessage(data.getDesc())
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //直接跳入主界面
                                    loadMain();
                                }
                            })
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //获取Url下载新版本,使用xUtils下载
                                    downAndInstall(data.getDownloadUrl());

                                }
                            });
                    builder.show();

                    break;
                case LOADMAIN:
                    //加载主界面
                    loadMain();
                    break;

                default:
                    break;
            }
        }

        /**
         * 下载，安装更新。
         * @param url
         */
        private void downAndInstall(String url) {
            HttpUtils httpUtils = new HttpUtils();
            //开始下载的时候显示进度条
            pb_download.setVisibility(View.VISIBLE);
            System.out.println("啦啦啦啦啦啦啦啦啦啦啦啦啦");
            httpUtils.download(url, "/sdcard/phoneguard.apk", new RequestCallBack<File>() {
                /*
                下载成功
                 */
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //开始下载apk，要跳转到下载apk的界面。需要查看源码中的实现下载的Activity是哪个。packages\apps\PackageInstaller
                    /*

                    <intent - filter >
                        <action android:name = "android.intent.action.VIEW" / >
                        <category android:name = "android.intent.category.DEFAULT" / >
                        <data android:scheme = "content" / >
                        <data android:scheme = "file" / >
                        <data android:mimeType = "application/vnd.android.package-archive" / >
                    </intent - filter >

                    */
                    //就是要启动上面的那个.PackageInstallerActivity
                    //下载成功之后，开始安装之前，让进度条消失
                    pb_download.setVisibility(View.GONE);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "phoneguard.apk")),
                            "application/vnd.android.package-archive");
                    //开启PackageInstallerActivity
                    //如果在这个界面用户不点击安装，而是选择了不安装，会返回来一个消息。
                    startActivityForResult(intent, 0);
                }


                /*
                    设置进度条
                 */
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    pb_download.setProgress((int) current);
                    pb_download.setMax((int) total);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    ShowToast.show(SplashActivity.this, "下载失败!");
                    loadMain();
                }
            });
        }


    };

    /**
     * 当用户在安装界面的时候选择了不安装，返回数据，直接跳转到主界面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadMain();
        super.onActivityResult(requestCode, resultCode, data);
    }

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
            conn = (HttpURLConnection) url.openConnection();
            //设置初始化参数
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //拿到响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                //请求成功
                //拿到服务器返回的输入流，也就是json数据，转换成字符串信息
                InputStream is = conn.getInputStream();
                String text = Stream2String.process(is);
                //弹出吐司。因为弹吐司是刷新UI，所以需要在主线程中运行，再次封装一个工具类。
                //解析json数据,
                UrlData urlData = ParseJson(text);
                //处理数据，判断版本号是否一致，如果不一致，说明有新版本
                if (versionCode != urlData.getVersionCode()) {
                    //使用对话框显示是否更新新版本
                    msg.what = UPDATEVERSION;
                    //发送消息，携带信息
                    msg.obj = urlData;

                } else {
                    //没有新版本，直接进入主界面,执行后面finally中的进入主界面

                }
            } else {
                //2000 请求失败，直接进入主界面
                ShowToast.show(SplashActivity.this, "2000 请求失败");

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            //2001 Url错误，直接进入主界面
            ShowToast.show(SplashActivity.this, "2001 Url错误");
        } catch (IOException e) {
            e.printStackTrace();
            //2002 IO错误，网络超时，直接进入主界面
            ShowToast.show(SplashActivity.this, "2002 IO错误，网络超时");
        } catch (JSONException e) {
            e.printStackTrace();
            //2003 json数据格式错误，直接进入主界面
            ShowToast.show(SplashActivity.this, "2003 json数据格式错误");
        } finally {
            if (msg.what == UPDATEVERSION) {
                //什么都不做
            } else {
                msg.what = LOADMAIN;
            }
            long endTime = System.currentTimeMillis();//耗时操作的结束时间
            //耗时操作的时间 = endTime - startTime
            //判断时间如果小于2秒，就休眠两秒，如果大于两秒，那么就该几秒几秒
            if ((endTime - startTime) < 2000) {
                //休眠
                SystemClock.setCurrentTimeMillis(2000 - (endTime - startTime));
            } else {
                //休眠时间超过2秒。不管了。
            }
            msg.sendToTarget();
        }
    }

    /**
     * 加载进入主界面
     */
    public void loadMain() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();//关闭Splash界面
    }

    /**
     * 解析json数据
     *
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
        AlphaAnimation aa = new AlphaAnimation(0, 1);
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
            tv_title.setText(versionName);

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
        //获取下载的进度条
        pb_download = (ProgressBar) findViewById(R.id.pb_splash_download);
    }
}
