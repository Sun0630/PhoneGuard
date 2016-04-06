package com.sx.phoneguard.activities;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sx.phoneguard.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CacheActivity extends AppCompatActivity {
    public static final int LOADING = 1;
    private static final int FINISH = 2;
    private LinearLayout ll_view;
    private TextView tv_title;
    private ProgressBar pb_loading;
    private TextView tv_nocache;
    private LinearLayout ll_datas;

    private List<CacheData> cacheDatas = new ArrayList<>();
    private PackageManager pm;
    private Button bt_clearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        startCache();
    }

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    ll_view.setVisibility(View.GONE);
                    tv_nocache.setVisibility(View.GONE);
                    bt_clearAll.setVisibility(View.GONE);
                    break;
                case FINISH:

                    if (cacheDatas.size() == 0) {//没有缓存
                        pb_loading.setVisibility(View.GONE);
                        ll_view.setVisibility(View.GONE);
                        tv_nocache.setVisibility(View.VISIBLE);
                        tv_title.setVisibility(View.GONE);
                        bt_clearAll.setVisibility(View.GONE);
                    } else {
                        pb_loading.setVisibility(View.GONE);
                        ll_view.setVisibility(View.VISIBLE);
                        tv_nocache.setVisibility(View.GONE);
                        bt_clearAll.setVisibility(View.VISIBLE);
                        //处理显示的数据
                        for (CacheData data :
                                cacheDatas) {
                            View v = View.inflate(getApplicationContext(), R.layout.item_cache_listview, null);
                            ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_cache_icon);
                            iv_icon.setImageDrawable(data.icon);
                            TextView tv_title = (TextView) v.findViewById(R.id.tv_cache_title);
                            tv_title.setText(data.name);
                            TextView tv_cache = (TextView) v.findViewById(R.id.tv_cache_memidirty);
                            tv_cache.setText(Formatter.formatFileSize(getApplicationContext(), data.size));

                            ll_datas.addView(v, 0);

                        }

                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 开始检索
     */
    private void startCache() {
        //开启子线程
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);

                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                for (PackageInfo info :
                        installedPackages) {
                    getCache(info.packageName);//获取缓存
                    //休眠
                    //SystemClock.sleep(100);
                }

                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
                super.run();
            }
        }.start();
    }

    /**
     * 任何aidl都是一样的处理方式：回调结果的函数
     */
    class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private String packName;//包名

        public MyIPackageStatsObserver(String packName) {
            this.packName = packName;
        }

        /**
         * 回调结果
         */
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            // TODO Auto-generated method stub
            System.out.println("缓存大小：" + pStats.cacheSize);
            if (pStats.cacheSize != 0) {//有缓存
                CacheData data = new CacheData();
                data.size = pStats.cacheSize;
                try {
                    data.name = pm.getPackageInfo(packName, 0).applicationInfo.loadLabel(pm) + "";
                    data.icon = pm.getPackageInfo(packName, 0).applicationInfo.loadIcon(pm);
                    cacheDatas.add(data);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * app的三个信息
     */
    private class CacheData {
        String name;
        Drawable icon;
        long size;
    }

    private void getCache(String packName){
        Class type = pm.getClass();
        try {
            Method method = type.getMethod("getPackageSizeInfo", new Class[]{String.class,
                    IPackageStatsObserver.class});

            //反射的本质：pm.getPackageSizeInfo
            method.invoke(pm, new Object[]{packName,new MyIPackageStatsObserver(packName)});

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 清除所有缓存的回调
     */
    class MyIPackageDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ll_datas.removeAllViews();//清空所有组件
                    Toast.makeText(getApplicationContext(), "所有缓存已被清除~", Toast.LENGTH_SHORT).show();
                    tv_nocache.setVisibility(View.VISIBLE);
                    ll_view.setVisibility(View.GONE);
                }
            });

        }
    }
    /**
     * 清除所有缓存
     * @param view
     */
    public void clearAll(View view){
        Class type = pm.getClass();
        try {
            Method method = type.getMethod("freeStorageAndNotify", new Class[]{long.class,
                    IPackageDataObserver.class});

            method.invoke(pm, new Object[]{Integer.MAX_VALUE,new MyIPackageDataObserver()});

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initView() {
        pm = getPackageManager();
        setContentView(R.layout.activity_cache);
        ll_view = (LinearLayout) findViewById(R.id.ll_cache_view);
        tv_title = (TextView) findViewById(R.id.tv_cache_title);
        pb_loading = (ProgressBar) findViewById(R.id.pb_cache_loading);
        tv_nocache = (TextView) findViewById(R.id.tv_cache_nocache);
        ll_datas = (LinearLayout) findViewById(R.id.ll_cache_datas);
        bt_clearAll = (Button) findViewById(R.id.bt_cache_clearAll);
    }
}
