package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.db.dao.BlackNumberDao;
import com.sx.phoneguard.domain.BlackNumberData;
import com.sx.phoneguard.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

public class CommunicationGuardActivityPage extends AppCompatActivity {

    private static final int LODING = 1;
    private static final int LODINGFINISH = 2;
    private LinearLayout ll_loading;
    private ListView lv_datas;
    private TextView tv_nodata;
    private BlackNumberDao dao;//提供对数据库的操作
    private List<BlackNumberData> datas = new ArrayList<>();
    private MyAdapter adapter;
    private EditText et_page;
    private TextView tv_page;
    private int totalPage;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();//初始化界面
        initData();//初始化数据
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
                    showPageMessage();//显示页码
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
                //datas = dao.findAll();//加载数据

                //获取总页数
                totalPage = dao.getPages();

                //获取当前页的数据
                datas = dao.getPageData(currentPage);

                try {
                    Thread.sleep(200);
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
        setContentView(R.layout.activity_communication_guard_page);
        ll_loading = (LinearLayout) findViewById(R.id.ll_blacklist_loading);
        lv_datas = (ListView) findViewById(R.id.lv_blacklist_datas);
        tv_nodata = (TextView) findViewById(R.id.tv_blacklist_nodata);
        dao = new BlackNumberDao(this);
        //为listView添加适配器
        adapter = new MyAdapter();
        lv_datas.setAdapter(adapter);

        et_page = (EditText) findViewById(R.id.et_black_page);
        tv_page = (TextView) findViewById(R.id.tv_black_page);
    }

    public class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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


            BlackNumberData data = datas.get(position);
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

    private class ViewHolder {
        TextView tv_black_number;
        TextView tv_black_mode;
        ImageView iv_black_delete;
    }

    /**
     * 上一页
     *
     * @param view
     */
    public void prev(View view) {
        if (currentPage == 1) {
            ShowToast.show(this, "这里是第一页");
        } else {
            currentPage--;
            initData();
        }

    }

    /**
     * 下一页
     *
     * @param view
     */
    public void next(View view) {
        if (currentPage == totalPage) {
            ShowToast.show(this, "已经是最后一页");
        } else {
            currentPage++;
            initData();
        }

    }

    /**
     * 跳转
     *
     * @param view
     */
    public void jump(View view) {
        int jumpPage = Integer.parseInt(et_page.getText().toString().trim());
        if (jumpPage < 0 || jumpPage > totalPage) {
            ShowToast.show(this,"没事别瞎转悠~");
        }else {
            currentPage = jumpPage;
            initData();
            et_page.setText("");
        }

    }

    /**
     * 显示页码   比如  4/10
     */
    public void showPageMessage(){
        String pageMessage = currentPage+"/"+totalPage;
        tv_page.setText(pageMessage);
    }
}
