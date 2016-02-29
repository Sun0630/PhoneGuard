package com.sx.phoneguard.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sx.phoneguard.engine.CommunicationLogEngine;
import com.sx.phoneguard.utils.MyConstants;

import java.util.List;

public class SmsActivity extends ListActivity {

    private List<String> datas;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lv = getListView();
        datas = CommunicationLogEngine.getSMSLog(this);
        //创建适配器
        MyAdapter adapter = new MyAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phonenumber = datas.get(position);
                //使用Intent携带数据
                Intent intent = new Intent();
                intent.putExtra(MyConstants.PHONENUMBER,phonenumber);
                setResult(0,intent);
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv_phone = null;
            if(convertView != null){
                tv_phone = (TextView) convertView;
            }else {
                tv_phone = new TextView(SmsActivity.this);
            }
            tv_phone.setText(datas.get(position));
            return tv_phone;
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
}
