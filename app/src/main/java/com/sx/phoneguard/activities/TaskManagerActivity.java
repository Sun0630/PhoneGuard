package com.sx.phoneguard.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sx.phoneguard.R;
import com.sx.phoneguard.domain.TaskBean;
import com.sx.phoneguard.engine.TaskEngine;
import com.sx.phoneguard.utils.MyConstants;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskManagerActivity extends AppCompatActivity {

    private static final int LOADING = 1 << 0;
    private static final int FINISH = 1 << 1;
    //界面声明
    private TextView tv_meminfo;
    private TextView tv_runningnumber;
    private TextView tv_tag;
    private ListView lv_datas;
    private LinearLayout ll_loading;
    //数据声明
    private long avaiMem;
    private long totalMemory;
    //用户进程
    /**
     * CopyOnWriteArrayList
     * 在对其实例进行修改操作（add/remove等）会新建一个数据并修改，
     * 修改完毕之后，再将原来的引用指向新的数组。这样，修改过程没有修改原来的数组。
     */
    private List<TaskBean> userDatas = new CopyOnWriteArrayList<>();
    //系统进程
    private List<TaskBean> sysDatas = new CopyOnWriteArrayList<>();

    private TaskManagerActivity.MyAdapter adapter;
    private ActivityManager am;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 根据位置判断tag的内容
             * @param view
             * 视图
             * @param firstVisibleItem
             * 第一条数据显示的位置
             * @param visibleItemCount
             * 可视条目的数量
             * @param totalItemCount
             * 总的条目数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当第一个条目显示的是系统进程的时候
                if (firstVisibleItem >= userDatas.size() + 1) {
                    //修改tag为系统进程
                    tv_tag.setText("系统进程(" + sysDatas.size() + ")");
                } else {//用户进程
                    tv_tag.setText("用户进程(" + userDatas.size() + ")");
                }
            }
        });
    }

    //定义消息队列处理数据刷新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING://正在加载
                    ll_loading.setVisibility(View.VISIBLE);
                    lv_datas.setVisibility(View.GONE);
                    tv_tag.setVisibility(View.GONE);
                    break;
                case FINISH://加载数据完成

                    updateView();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void updateView() {
        //这里不可能没有数据，没有数据就不作处理。
        ll_loading.setVisibility(View.GONE);
        lv_datas.setVisibility(View.VISIBLE);
        tv_tag.setVisibility(View.VISIBLE);

        if (!sp.getBoolean(MyConstants.SHOWSYSTEM, false)) {
            tv_runningnumber.setText("运行中的进程:" + (userDatas.size()) + "个");
        } else {
            tv_runningnumber.setText("运行中的进程:" + (userDatas.size() + sysDatas.size()) + "个");
        }
        //格式化单位为MB
        String avaiMemF = Formatter.formatFileSize(getApplicationContext(), avaiMem);
        String totalMemF = Formatter.formatFileSize(getApplicationContext(), totalMemory);
        tv_meminfo.setText("可用/总内存:" + (avaiMemF + "/" + totalMemF));

        if (adapter == null) {
            adapter = new MyAdapter();
            lv_datas.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (!sp.getBoolean(MyConstants.SHOWSYSTEM, false)) {//不显示系统进程
                return userDatas.size() + 1;
            }

            return userDatas.size() + 1 + sysDatas.size() + 1;
        }

        @Override
        public TaskBean getItem(int position) {
            if (position <= userDatas.size()) {//用户进程
                return userDatas.get(position - 1);
            } else {//系统进程
                return sysDatas.get(position - (userDatas.size() + 2));
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {//用户进程
                TextView tv = new TextView(getApplicationContext());
                tv.setText("用户进程(" + userDatas.size() + ")");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position == userDatas.size() + 1) {//系统进程
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统进程(" + sysDatas.size() + ")");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                return tv;
            } else {
                //获取数据
                final TaskBean bean = getItem(position);
                Tag tag = new Tag();
                if (convertView == null || convertView instanceof TextView) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_task_manager_listview, null);
                    tag.tv_memidirty = (TextView) convertView.findViewById(R.id.tv_task_manager_memidirty);
                    tag.tv_title = (TextView) convertView.findViewById(R.id.tv_task_manager_title);
                    tag.iv_icon = (ImageView) convertView.findViewById(R.id.iv_task_manager_icon);
                    tag.cb_checked = (CheckBox) convertView.findViewById(R.id.cb_task_manager_checked);
                    tag.rl_task_setting = (RelativeLayout) convertView.findViewById(R.id.rl_task_setting);


                    convertView.setTag(tag);
                } else {
                    tag = (Tag) convertView.getTag();
                }

                tag.tv_title.setText(bean.getName());
                tag.tv_memidirty.setText("内存占用:" + Formatter.formatFileSize(getApplicationContext(), bean.getSize()));
                tag.iv_icon.setImageDrawable(bean.getIcon());

                //为CheckBox设置监听
                tag.cb_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bean.setIsChecked(isChecked);
                    }
                });
                final Tag finalTag = tag;
                tag.rl_task_setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //如果是自己，让复选框隐藏
                        if (bean.getPackName().equals(getPackageName())) {
                            return;
                        }
                        //取反
                        finalTag.cb_checked.setChecked(!finalTag.cb_checked.isChecked());
                    }
                });

                //应该从bean中取出复选框状态
                tag.cb_checked.setChecked(bean.isChecked());
                //如果是自己，让复选框隐藏
                if (bean.getPackName().equals(getPackageName())) {
                    tag.cb_checked.setVisibility(View.GONE);
                } else {
                    tag.cb_checked.setVisibility(View.VISIBLE);
                }
                return convertView;
            }

        }
    }

    /**
     * 当界面回复显示的时候调用，
     */
    @Override
    protected void onResume() {
        updateView();
        super.onResume();
    }

    private class Tag {
        private TextView tv_memidirty;
        private CheckBox cb_checked;
        private TextView tv_title;
        private ImageView iv_icon;
        private RelativeLayout rl_task_setting;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //取数据
        new Thread() {
            @Override
            public void run() {
                //发送正在加载的消息
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);

                avaiMem = TaskEngine.getAvaiMem(getApplicationContext());
                totalMemory = TaskEngine.getTotalMemory(getApplicationContext());
                List<TaskBean> runningProcess = TaskEngine.getRunningProcess(getApplicationContext());
                for (TaskBean bean : runningProcess) {
                    if (bean.isSystem()) {//是系统进程
                        sysDatas.add(bean);
                    } else {//用户进程
                        userDatas.add(bean);
                    }
                }

                //模拟加载时间500毫秒
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //发送加载完成的消息
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);

                super.run();
            }
        }.start();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_task_manager);
        tv_meminfo = (TextView) findViewById(R.id.tv_task_manager_meminfo);
        tv_runningnumber = (TextView) findViewById(R.id.tv_task_manager_runningnumber);
        tv_tag = (TextView) findViewById(R.id.tv_task_tag);
        lv_datas = (ListView) findViewById(R.id.lv_task_manager_datas);
        ll_loading = (LinearLayout) findViewById(R.id.ll_task_manager_loading);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
    }

    /**
     * 清理进程
     *
     * @param view
     */
    public void clear(View view) {
        int number = 0;//清理的进程个数
        long clearMem = 0;

        //遍历容器中的所有数据
        for (TaskBean bean : userDatas) {
            //如果该程序的复选框被选中就要被清理
            if (bean.isChecked()) {
                am.killBackgroundProcesses(bean.getPackName());
                //有些进程杀不掉，就从容器中移除出去就行
                //因为在遍历集合的时候不能修改集合的结构，这里使用CopyOnWriteArrayList();方法，
                //这个方法是先把集合中的数据拷贝一份，然后
                userDatas.remove(bean);
                number++;
                clearMem += bean.getSize();
            }
        }

        for (TaskBean bean : sysDatas) {
            if (bean.isChecked()) {
                am.killBackgroundProcesses(bean.getPackName());
                sysDatas.remove(bean);
                number++;
                clearMem += bean.getSize();
            }
        }


        avaiMem += clearMem;
        updateView();//更新界面
        Toast.makeText(getApplicationContext(), "共清理了" + number + "个进程"
                        + "释放了" + Formatter.formatFileSize(getApplicationContext(), clearMem) + "内存！",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {
        //遍历容器中的所有数据，然后设置CheckBox
        for (TaskBean bean :
                userDatas) {
            //不能选择自己
            if (bean.getPackName().equals(getPackageName())) {
                bean.setIsChecked(false);
            } else {
                bean.setIsChecked(true);
            }
        }

        for (TaskBean bean :
                sysDatas) {
            //不能选择自己
            if (bean.getPackName().equals(getPackageName())) {
                bean.setIsChecked(false);
            } else {
                bean.setIsChecked(true);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void inserveSelect(View view) {
        for (TaskBean bean :
                userDatas) {
            //不能选择自己
            if (bean.getPackName().equals(getPackageName())) {
                bean.setIsChecked(false);
            } else {
                bean.setIsChecked(!bean.isChecked());
            }
        }

        for (TaskBean bean :
                sysDatas) {
            //不能选择自己
            if (bean.getPackName().equals(getPackageName())) {
                bean.setIsChecked(false);
            } else {
                bean.setIsChecked(!bean.isChecked());
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 设置
     *
     * @param view
     */
    public void setting(View view) {
        /**
         * 新打开一个Activity
         *  1，是否显示系统进程
         *  2，是否开启锁屏清理进程
         */

        Intent intent = new Intent(this,TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
