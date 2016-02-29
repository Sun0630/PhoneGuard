package com.sx.phoneguard.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.db.dao.BlackNumberDao;
import com.sx.phoneguard.domain.BlackNumberData;
import com.sx.phoneguard.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

public class CommunicationGuardActivity extends AppCompatActivity {

    private static final int LODING = 1;
    private static final int LODINGFINISH = 2;
    private LinearLayout ll_loading;
    private ListView lv_datas;
    private TextView tv_nodata;
    private BlackNumberDao dao;//提供对数据库的操作
    private List<BlackNumberData> datas = new ArrayList<>();
    private MyAdapter adapter;
    private AlertDialog dialog;
    private PopupWindow pw;
    private View popupView;
    private ScaleAnimation sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();//初始化界面
        initPopupView();//初始化窗体功能列表
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initEvent() {
        //分批处理事件，为ListView添加一个滑动监听
        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 状态改变
             * @param view
             *      就是ListView
             * @param scrollState
             *      滑动的状态：包括三种
             *       AbsListView.OnScrollListener.SCROLL_STATE_FLING;  惯性滑动
             *       AbsListView.OnScrollListener.SCROLL_STATE_IDLE;   静止
             *       AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;  触摸滑动
             *
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING://惯性滑动
                        break;
                    case SCROLL_STATE_IDLE://静止
                        //当滑动静止的时候判断是否是ListView的最后一条记录
                        int lastVisiblePosition = view.getLastVisiblePosition();//ListView的最后一条记录的位置
                        if (lastVisiblePosition == dao.getTotal() - 1) {
                            ShowToast.show(CommunicationGuardActivity.this, "没有更多数据了");
                            return;
                        }
                        if (lastVisiblePosition == datas.size() - 1) {
                            initData();//动态加载更多的数据
                        }

                        break;
                    case SCROLL_STATE_TOUCH_SCROLL://触摸滑动
                        break;
                }
            }

            //滑动
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    //新建一个消息队列
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LODING:
                    //加载中
                    ll_loading.setVisibility(View.VISIBLE);
                    break;
                case LODINGFINISH://接受数据完成
                    ll_loading.setVisibility(View.GONE);
                    if (datas.size() == 0) { //没数据
                        tv_nodata.setVisibility(View.VISIBLE);
                        lv_datas.setVisibility(View.GONE);
                    } else {//有数据
                        tv_nodata.setVisibility(View.GONE);
                        lv_datas.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }

        }
    };

    private void initData() {
        //加载数据是个耗时的操作，要封装到子线程中
        new Thread() {
            @Override
            public void run() {
                //正在加载数据
                Message msg = Message.obtain();
                msg.what = LODING;
                handler.sendMessage(msg);

                List<BlackNumberData> appDatas = dao.getData(datas.size() + "", 20 + "");//加载数据,返回一个添加的数据
                datas.addAll(appDatas);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //发送数据
                msg = Message.obtain();
                msg.what = LODINGFINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_communication_guard);
        ll_loading = (LinearLayout) findViewById(R.id.ll_blacklist_loading);
        lv_datas = (ListView) findViewById(R.id.lv_blacklist_datas);
        tv_nodata = (TextView) findViewById(R.id.tv_blacklist_nodata);
        dao = new BlackNumberDao(this);
        //为listView添加适配器
        adapter = new MyAdapter();
        lv_datas.setAdapter(adapter);
    }

    /**
     * 添加按钮事件
     *
     * @param view 按钮事件
     */
    public void addData(View view) {
        //弹出窗体，然后让用户选择从哪里添加黑名单联系人，可以从通话记录，短信记录中选取
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {
            pw.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 20, 110);
            popupView.startAnimation(sa);
        }

    }

    private void initPopupView() {
        popupView = View.inflate(getApplicationContext(), R.layout.popupview, null);
        //拿到四个TextView为其添加点击事件
        TextView tv_contacts = (TextView) popupView.findViewById(R.id.tv_contacts); //从联系人
        TextView tv_diy = (TextView) popupView.findViewById(R.id.tv_diy); //手动添加
        TextView tv_sms = (TextView) popupView.findViewById(R.id.tv_sms); //短信列表添加
        TextView tv_remember = (TextView) popupView.findViewById(R.id.tv_remember);//通话记录添加

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_contacts://从联系人导入
                        Intent contacts = new Intent(CommunicationGuardActivity.this,FriendsActivity.class);
                        startActivityForResult(contacts,0);
                        break;
                    case R.id.tv_diy://手动添加
                        addData("");
                        break;
                    case R.id.tv_sms://从短信列表导入
                        //取到短信记录的数据
                        //界面显示
                        Intent sms = new Intent(CommunicationGuardActivity.this,SmsActivity.class);
                        startActivityForResult(sms,0);
                        break;
                    case R.id.tv_remember://从通话记录导入
                        //取到通话记录的数据
                        //界面显示
                        Intent tel = new Intent(CommunicationGuardActivity.this,TelActivity.class);
                        startActivityForResult(tel,0);
                        break;
                }
                //点击完对话框都得消失
                pw.dismiss();
            }
        };

        tv_contacts.setOnClickListener(listener);
        tv_diy.setOnClickListener(listener);
        tv_sms.setOnClickListener(listener);
        tv_remember.setOnClickListener(listener);


        //弹出窗体功能列表
        pw = new PopupWindow(popupView, -2, -2);//设置这个窗体大小为包裹内容
        //窗体必须有背景才能显示动画效果
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//把背景设置成透明的
        sa = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        sa.setDuration(200);
        popupView.setAnimation(sa);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            String phonenumber = data.getStringExtra("phonenumber");
            addData(phonenumber);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 手动添加数据
     */
    public void addData(String phonenumber) {
        //弹出一个添加信息的对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //填充一个布局组件
        View view1 = View.inflate(this, R.layout.dialog_addblack, null);
        //获取组件
        final EditText et_phoneNumber = (EditText) view1.findViewById(R.id.et_black_phonenumber);
        final CheckBox cb_sms = (CheckBox) view1.findViewById(R.id.cb_black_sms);
        final CheckBox cb_phone = (CheckBox) view1.findViewById(R.id.cb_black_phone);
        Button bt_add = (Button) view1.findViewById(R.id.bt_black_add);
        Button bt_cancel = (Button) view1.findViewById(R.id.bt_black_cancle);
        /*
        确定的事件
         */

        et_phoneNumber.setText(phonenumber);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = et_phoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ShowToast.show(CommunicationGuardActivity.this, "号码不能为空");
                    return;
                }

                int mode = 0;
                if (cb_sms.isChecked()) {
                    mode |= BlackNumberData.SMS;
                }
                if (cb_phone.isChecked()) {
                    mode |= BlackNumberData.PHONE;
                }
                if (mode == 0) {
                    ShowToast.show(CommunicationGuardActivity.this, "至少选择一种拦截模式");
                    return;
                }

                //保存数据到数据库
                BlackNumberData data = new BlackNumberData(phoneNumber, mode);
                dao.add(data);
                datas.clear();//清空
                initData();//重新加载数据
                dialog.dismiss();

            }
        });

        /*
        取消的事件
         */
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setView(view1);
        dialog.show();
    }

    public class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_black_list, null);
                holder = new ViewHolder();
                holder.tv_black_number = (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tv_black_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
                holder.iv_black_delete = (ImageView) convertView.findViewById(R.id.iv_black_delete);

                //设置
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final BlackNumberData data = datas.get(position);
            //设置数据,黑名单号码
            holder.tv_black_number.setText(data.getBlackNumber());
            //设置模式
            switch (data.getMode()) {
                case BlackNumberData.SMS:
                    holder.tv_black_mode.setText("短信拦截");
                    break;
                case BlackNumberData.PHONE:
                    holder.tv_black_mode.setText("电话拦截");
                    break;
                case BlackNumberData.ALL:
                    holder.tv_black_mode.setText("全部拦截");
                    break;
                default:
                    holder.tv_black_mode.setText("不拦截");
                    break;
            }
            //删除的事件
            holder.iv_black_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出对话框提示
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommunicationGuardActivity.this);
                    builder.setTitle("友情提示");
                    builder.setMessage("您真的要删除" + data.getBlackNumber() + "吗?");
                    builder.setPositiveButton("真的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteBlackNumber(position);
                        }
                    });

                    builder.setNegativeButton("假的", null);

                    builder.show();
                }
            });


            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }

    private void deleteBlackNumber(int position) {
        dao.remove(datas.get(position));//删除数据库中的数据
        datas.remove(position);//删除集合中的数据
        //界面更新数据
        adapter.notifyDataSetChanged();//删除完数据之后还会显示在原来数据的位置。
    }

    private class ViewHolder {
        TextView tv_black_number;
        TextView tv_black_mode;
        ImageView iv_black_delete;
    }

    @Override
    protected void onDestroy() {
        //销毁popupWindow
        //当popup正在显示的时候，如果用户点击了后退键，会造成内存问题。所以需要释放
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
        super.onDestroy();
    }
}
