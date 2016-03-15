package com.sx.phoneguard.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;
import com.sx.phoneguard.R;
import com.sx.phoneguard.domain.AppBean;
import com.sx.phoneguard.engine.AppEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LODING = 1 << 0;
    private static final int FINISH = 1 << 1;
    private LinearLayout ll_loading;
    private ListView lv_datas;
    private List<AppBean> userApps = new ArrayList<>();//用户软件的集合
    private List<AppBean> systemApps = new ArrayList<>();//系统软件的集合
    private MyAdapter adapter;
    private TextView tv_rom;
    private TextView tv_sd;
    private TextView tv_tag;
    private PopupWindow pw;//弹出的窗体
    private AppBean clickBean;//点击获取数据
    private PackageManager pm;
    private RemoveAppReceiver receiver;
    private long romSize;
    private long sdSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //只初始化一次
        initSDROMData();
        //需要初始化多次
        initData();
        initEvent();
    }

    private void closePW() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
    }

    private void initPW(View parent) {
        View view = View.inflate(getApplicationContext(), R.layout.popup_appmanager, null);
        pw = new PopupWindow(view, -2, -2);
        //弹出窗体显示动画，必须要设置背景
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int[] location = new int[2];
        parent.getLocationInWindow(location);
        pw.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, location[0] + 80, location[1]);

        //获得四个组件

        LinearLayout ll_share = (LinearLayout) view.findViewById(R.id.ll_share);
        LinearLayout ll_unistanll = (LinearLayout) view.findViewById(R.id.ll_uninstall);
        LinearLayout ll_setting = (LinearLayout) view.findViewById(R.id.ll_setting);
        LinearLayout ll_launcher = (LinearLayout) view.findViewById(R.id.ll_launcher);

        //设置事件监听
        ll_share.setOnClickListener(this);
        ll_unistanll.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_launcher.setOnClickListener(this);


        //设置透明度渐变动画
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(700);
        aa.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                float y = 0;
                return 10 * x;
            }
        });

        ScaleAnimation sa = new ScaleAnimation(0, 1, 0.5f, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);

        AnimationSet as = new AnimationSet(true);
        as.addAnimation(aa);
        as.addAnimation(sa);

        view.startAnimation(as);
    }


    private void initEvent() {

        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == userApps.size() + 1) {
                    return;
                }
                closePW();
                initPW(view);
                //内部调用adapter的getItem()方法。
                clickBean = (AppBean) lv_datas.getItemAtPosition(position);
            }
        });

        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                closePW();
            }

            /**
             * 只要ListView滑动，这个方法就要调用
             * @param view
             * view
             * @param firstVisibleItem
             * 第一条显示数据的位置
             * @param visibleItemCount
             * 可视条目的数量
             * @param totalItemCount
             * 总的条目数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当第一条显示的数据位置是系统软件那个位置的时候
                if (firstVisibleItem >= userApps.size() + 1) {
                    //修改tag为系统软件
                    tv_tag.setText("系统软件(" + systemApps.size() + ")");
                } else {
                    tv_tag.setText("个人软件(" + userApps.size() + ")");
                }
            }
        });
    }

    private void initSDROMData() {
        //取数据
        romSize = AppEngine.getRom(getApplicationContext());
        sdSize = AppEngine.getSd(getApplicationContext());

        String romMsg = Formatter.formatFileSize(getApplicationContext(), romSize);
        String sdMsg = Formatter.formatFileSize(getApplicationContext(), sdSize);

        //设置到界面
        tv_rom.setText("Rom大小为:" + romMsg);
        tv_sd.setText("Sd卡大小为:" + sdMsg);
    }

    //消息队列
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LODING://正在加载数据
                    lv_datas.setVisibility(View.GONE);
                    ll_loading.setVisibility(View.VISIBLE);
                    break;
                case FINISH://加载数据完成
                    lv_datas.setVisibility(View.VISIBLE);
                    ll_loading.setVisibility(View.GONE);

                    tv_tag.setVisibility(View.VISIBLE);
                    if (adapter == null) {
                        adapter = new MyAdapter();
                        lv_datas.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_uninstall://卸载软件
                uninstall();
                break;

            case R.id.ll_share://分享软件
                share();
                break;

            case R.id.ll_setting://设置
                setting();
                break;

            case R.id.ll_launcher://启动软件
                launcher();
                break;
        }
        closePW();
    }

    /**
     * 卸载
     */
    public void uninstall() {
        //判断卸载的是用户软件还是系统软件
        if (clickBean.isSystem()) {//系统软件卸载
            try {
                if (!RootTools.isRootAvailable()) {//没有root权限
                    Toast.makeText(getApplicationContext(), "没有root权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!RootTools.isAccessGiven()) {
                    Toast.makeText(getApplicationContext(), "请赋予我root权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                RootTools.sendShell("mount -o remount rw/system",30000);
                RootTools.sendShell("rm -r "+clickBean.getPath(),30000);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootToolsException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//用户软件卸载
            Intent intent = new Intent();
            intent.setAction("android.intent.action.DELETE   ");
            intent.addCategory("android.intent.action.DEFAULT");
            intent.setData(Uri.parse("package:" + clickBean.getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * 启动应用
     */
    public void launcher() {
        //        System.out.println(clickBean.getPackageName()+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Intent intent = pm.getLaunchIntentForPackage(clickBean.getPackageName());
        startActivity(intent);
    }

    public void share() {
        //分享：发送短信链接
        /**
         * <intent-filter>
         *     <action android:name="android.intent.action.SEND" />
         *     <category android:name="android.intent.category.DEFAULT" />
         *     <data android:mimeType="text/plain" />
         * </intent-filter>
         */

        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "lianjie");
        startActivity(intent);
    }

    /**
     * 设置
     */
    public void setting() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + clickBean.getPackageName()));
        startActivity(intent);
    }


    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userApps.size() + 1 + systemApps.size() + 1;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = null;
            if (position == 0) {
                v = new TextView(getApplicationContext());
                ((TextView) v).setText("个人软件(" + userApps.size() + ")");
                ((TextView) v).setTextColor(Color.WHITE);
                ((TextView) v).setBackgroundColor(Color.GRAY);
                return v;
            } else if (position == userApps.size() + 1) {
                v = new TextView(getApplicationContext());
                ((TextView) v).setText("系统软件(" + systemApps.size() + ")");
                ((TextView) v).setTextColor(Color.WHITE);
                ((TextView) v).setBackgroundColor(Color.GRAY);
                return v;
            } else {
                //判断position
                AppBean bean = getItem(position);
                Tag tag = null;
                if (convertView == null || convertView instanceof TextView) {
                    v = View.inflate(getApplicationContext(), R.layout.item_appmanager_listview, null);
                    tag = new Tag();
                    tag.tv_location = (TextView) v.findViewById(R.id.tv_appmanager_location);
                    tag.tv_size = (TextView) v.findViewById(R.id.tv_appmanager_size);
                    tag.iv_icon = (ImageView) v.findViewById(R.id.iv_appmanager_icon);
                    tag.tv_title = (TextView) v.findViewById(R.id.tv_appmanager_title);
                    v.setTag(tag);
                } else {
                    v = convertView;
                    tag = (Tag) v.getTag();
                }

                if (tag != null) {
                    tag.tv_title.setText(bean.getName());
                    if (bean.isRom()) {//内部存储
                        tag.tv_location.setText("手机内存");
                    } else {
                        tag.tv_location.setText("sd卡内存");
                    }

                    tag.tv_size.setText(Formatter.formatFileSize(getApplicationContext(), bean.getSize()));

                    tag.iv_icon.setImageDrawable(bean.getIcon());
                }
            }
            return v;
        }

        @Override
        public AppBean getItem(int position) {
            AppBean bean = null;
            if (position <= userApps.size()) {
                bean = userApps.get(position - 1);
            } else {
                bean = systemApps.get(position - (userApps.size() + 2));
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private class Tag {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_size;
        TextView tv_location;
    }

    /**
     * 初始化所有APP的信息
     */
    private void initData() {
        //取数据，是耗时操作
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = LODING;
                handler.sendMessage(msg);

                //取数据
                List<AppBean> allAllApps = AppEngine.getAllAllApps(getApplicationContext());
                for (AppBean b : allAllApps) {
                    if (b.isSystem()) {//如果是系统软件
                        systemApps.add(b);
                    } else {//用户软件
                        userApps.add(b);
                    }
                }

                //睡眠一秒钟之后再发次消息
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //发消息
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    public class RemoveAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //重新读取数据，更新界面
            //直接从内存删
            //如果通过命令来删除，clickBean就会为空，软件就会报错。
            if (clickBean == null) {
                return;
            }
            if (clickBean.isSystem()) {
                systemApps.remove(clickBean);
            } else {
                userApps.remove(clickBean);
            }

            //通知视图更新
            adapter.notifyDataSetChanged();
            //更新存储信息
            initSDROMData();

        }
    }

    private void initView() {
        setContentView(R.layout.activity_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_appmanager_loading);
        lv_datas = (ListView) findViewById(R.id.lv_appmanager_datas);
        tv_rom = (TextView) findViewById(R.id.tv_appmanager_rom);
        tv_sd = (TextView) findViewById(R.id.tv_appmanager_sd);
        tv_tag = (TextView) findViewById(R.id.tv_manager_tag);
        pm = getPackageManager();
        //注册一个广播接收者,接收卸载应用时的广播
        receiver = new RemoveAppReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        closePW();
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
