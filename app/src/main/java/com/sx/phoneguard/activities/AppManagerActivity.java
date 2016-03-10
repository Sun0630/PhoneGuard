package com.sx.phoneguard.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.sx.phoneguard.R;
import com.sx.phoneguard.domain.AppBean;
import com.sx.phoneguard.engine.AppEngine;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

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

    public void uninstall(View view) {

    }

    public void launcher(View view) {

    }

    public void share(View view) {

    }

    public void setting(View view) {

    }

    private void initEvent() {

        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closePW();
                initPW(view);
            }
        });

        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

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
        long romSize = AppEngine.getRom(getApplicationContext());
        long sdSize = AppEngine.getSd(getApplicationContext());

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

    private void initView() {
        setContentView(R.layout.activity_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_appmanager_loading);
        lv_datas = (ListView) findViewById(R.id.lv_appmanager_datas);
        tv_rom = (TextView) findViewById(R.id.tv_appmanager_rom);
        tv_sd = (TextView) findViewById(R.id.tv_appmanager_sd);
        tv_tag = (TextView) findViewById(R.id.tv_manager_tag);
    }
}
