package com.sx.phoneguard.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sx.phoneguard.R;

public class HomeActivity extends AppCompatActivity {

    private GridView gv_jiugongge;
    private GVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化数据
    }

    private void initData() {
        adapter = new GVAdapter();
        gv_jiugongge.setAdapter(adapter);
    }

    String[] names = {"手机防盗", "通讯卫士", "软件管家",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"};

    int[] icons = {R.drawable.safe,R.drawable.callmsgsafe,R.drawable.icon_selector,
            R.drawable.taskmanager,R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
    };

    private void initView() {
        setContentView(R.layout.activity_home);
        gv_jiugongge = (GridView) findViewById(R.id.gv_home_jiugongge);
    }

    class GVAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

/*        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //使用自定义的View对象
            View view = View.inflate(getApplicationContext(),R.layout.item_home_gridview,null);
            //里面包含两个组件
            ImageView iv = (ImageView) view.findViewById(R.id.iv_girdview_item_icon);
            TextView tv = (TextView) view.findViewById(R.id.tv_gridview_item_name);

            //为这两个组件设置显示内容
            iv.setBackgroundResource(icons[position]);
            tv.setText(names[position]);
            return view;
        }*/

        /**
         * 使用缓存
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_home_gridview, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_girdview_item_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_gridview_item_name);
                //绑定子组件
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            //为这两个组件设置显示内容
            holder.iv_icon.setBackgroundResource(icons[position]);
            holder.tv_name.setText(names[position]);
            return convertView;
        }

        private class ViewHolder{
            private ImageView iv_icon;
            private TextView tv_name;
        }
    }
}
