package com.sx.phoneguard.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.db.dao.LockDao;
import com.sx.phoneguard.domain.AppBean;
import com.sx.phoneguard.engine.AppEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ad on 2016/3/22.
 * 未加锁的Fragment
 */
public class UnLockFragment extends Fragment {

    private static final int LOADING = 1;
    private static final int FINISH = 0;
    private TextView tv_message;
    private ListView lv_datas;
    private LinearLayout ll_datas;
    private ProgressBar pb_loading;
    private List<AppBean> unlock_datas;
    private List<AppBean> user_unlock_datas = new ArrayList<>();
    private List<AppBean> sys_unlock_datas = new ArrayList<>();

    private MyAdapter adapter;
    private LockDao dao;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            dao = new LockDao(getActivity());
        }
    }

    //创建View的时候调用
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unlock, null);
        tv_message = (TextView) view.findViewById(R.id.tv_fragment_unlockNumber);
        lv_datas = (ListView) view.findViewById(R.id.lv_fragment_unlock_datas);
        ll_datas = (LinearLayout) view.findViewById(R.id.ll_fragment_datas);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_fragment_loading);

        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    ll_datas.setVisibility(View.GONE);

                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    ll_datas.setVisibility(View.VISIBLE);
                    tv_message.setText("未加锁软件(" + unlock_datas.size() + ")");

                    /*if (adapter == null) {
                        adapter = new MyAdapter();
                        lv_datas.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }*/

                    adapter = new MyAdapter();
                    lv_datas.setAdapter(adapter);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sys_unlock_datas.size() + 1 + user_unlock_datas.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position <= user_unlock_datas.size()) {
                return user_unlock_datas.get(position - 1);
            } else {
                return sys_unlock_datas.get(position - user_unlock_datas.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = null;
            if (position == 0) {
                v = new TextView(getActivity());
                ((TextView) v).setText("个人软件(" + user_unlock_datas.size() + ")");
                ((TextView) v).setTextColor(Color.WHITE);
                ((TextView) v).setBackgroundColor(Color.GRAY);
                return v;
            } else if (position == user_unlock_datas.size() + 1) {
                v = new TextView(getActivity());
                ((TextView) v).setText("系统软件(" + sys_unlock_datas.size() + ")");
                ((TextView) v).setTextColor(Color.WHITE);
                ((TextView) v).setBackgroundColor(Color.GRAY);
                return v;
            } else {
                //判断position
                final AppBean bean = (AppBean) getItem(position);
                Tag tag = null;
                if (convertView == null || convertView instanceof TextView) {
                    v = View.inflate(getActivity(), R.layout.item_fragment_unlock, null);
                    tag = new Tag();
                    tag.iv_lock = (ImageView) v.findViewById(R.id.iv_fragment_lock);
                    tag.iv_icon = (ImageView) v.findViewById(R.id.iv_fragment_icon);
                    tag.tv_title = (TextView) v.findViewById(R.id.tv_fragment_title);
                    v.setTag(tag);
                } else {
                    v = convertView;
                    tag = (Tag) v.getTag();
                }
                final View w = v;
                if (tag != null) {
                    tag.tv_title.setText(bean.getName());
                    tag.iv_icon.setImageDrawable(bean.getIcon());
                    tag.iv_lock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vv) {
                            //添加动画
                            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                                    Animation.RELATIVE_TO_SELF, 1,
                                    Animation.RELATIVE_TO_SELF, 0,
                                    Animation.RELATIVE_TO_SELF, 0);

                            ta.setDuration(300);
                            w.startAnimation(ta);
                            new Thread(){
                                @Override
                                public void run() {
                                    SystemClock.sleep(300);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //添加加锁的数据
                                            addLockData(bean);
                                        }
                                    });
                                    super.run();
                                }
                            }.start();
                        }

                        private void addLockData(AppBean bean) {
                            //删除当前界面数据
                            if (bean.isSystem()) {
                                sys_unlock_datas.remove(bean);
                            } else {
                                user_unlock_datas.remove(bean);
                            }
                            //把数据加到数据库中
                            dao.add(bean.getPackageName());
                            //更新界面
                            adapter.notifyDataSetChanged();
                            //更新数据的显示
                            tv_message.setText("未加锁软件(" + (user_unlock_datas.size() + sys_unlock_datas.size()) + ")");
                        }
                    });
                }
            }
            return v;
        }
    }

    private class Tag {
        ImageView iv_icon;
        TextView tv_title;
        ImageView iv_lock;
    }

    @Override
    public void onStart() {
        initData();
        super.onStart();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);


                //获取到所有app
                unlock_datas = AppEngine.getAllAllApps(getActivity());

                //过滤掉加锁的app
                Iterator<AppBean> iterator = unlock_datas.iterator();
                while (iterator.hasNext()) {
                    AppBean bean = iterator.next();
                    if (dao.isLocked(bean.getPackageName())) {//判断包是否在加锁的表中
                        iterator.remove();//删除next方法返回的数据
                    }
                }
                //加数据之前先要清除数据
                sys_unlock_datas.clear();
                user_unlock_datas.clear();

                for (AppBean bean :
                        unlock_datas) {
                    if (bean.isSystem()) {
                        sys_unlock_datas.add(bean);
                    } else {
                        user_unlock_datas.add(bean);
                    }
                }


                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
                super.run();
            }
        }.start();
    }
}
