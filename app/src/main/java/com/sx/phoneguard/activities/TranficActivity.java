package com.sx.phoneguard.activities;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.ShowToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TranficActivity extends ListActivity {

    private ActivityManager am;
    private PackageManager pm;
    private List<Data> datas = new ArrayList<>();
    private MyAdapter adapter;
    private ListView lv_datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1，列出所有APP(图标，名字 uid)
        lv_datas = getListView();
        initData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (adapter == null){
                adapter = new MyAdapter();
                lv_datas.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
            super.handleMessage(msg);
        }
    };

    private void initData() {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        pm = getPackageManager();
        new Thread() {
            @Override
            public void run() {

                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo :
                        installedPackages) {

                    Data d = new Data();

                    d.icon = packageInfo.applicationInfo.loadIcon(pm);
                    d.name = packageInfo.applicationInfo.loadLabel(pm) + "";
                    d.uid = packageInfo.applicationInfo.uid;

                    datas.add(d);
                }
                handler.obtainMessage().sendToTarget();
                super.run();
            }
        }.start();

    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Tag tag = null;
            if (convertView == null) {
                //缓存为空，创建组件

                convertView = View.inflate(getApplicationContext(), R.layout.item_tranfic_view, null);
                tag = new Tag();
                tag.iv_icon = (ImageView) convertView.findViewById(R.id.iv_tranfic_appicon);
                tag.tv_name = (TextView) convertView.findViewById(R.id.tv_tranfic_apptitle);
                tag.iv_see = (ImageView) convertView.findViewById(R.id.iv_tranfic_ok);

                convertView.setTag(tag);
            } else {
                tag = (Tag) convertView.getTag();
            }
            tag.iv_icon.setImageDrawable(datas.get(position).icon);
            tag.tv_name.setText(datas.get(position).name);
            tag.iv_see.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = "/proc/uid_stat/" + datas.get(position).uid + "/tcp_rcv";//下载数据的流量
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
                        String size = br.readLine();
                        ShowToast.show(TranficActivity.this, "下载的流量为:" + Formatter.formatFileSize(getApplicationContext(), Long.parseLong(size)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }
    }

    class Tag {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_see;
    }

    class Data {
        Drawable icon;
        String name;
        int uid;
    }
}
