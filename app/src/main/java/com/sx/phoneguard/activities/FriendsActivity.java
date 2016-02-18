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

import com.sx.phoneguard.R;
import com.sx.phoneguard.domain.ContactInfo;
import com.sx.phoneguard.utils.ContactInfoParser;

import java.util.List;

public class FriendsActivity extends ListActivity {

    private List<ContactInfo> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = getListView();
        MyAdapter adapter = new MyAdapter();
        datas = ContactInfoParser.findAll(this);
        listView.setAdapter(adapter);
        //获取系统所有联系人的信息


        //为条目设置点击监听，点击该条目，把该条目的信息显示在编辑框中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = datas.get(position).getPhone();
                //使用Intent来发送消息回去
                Intent d = new Intent();
                d.putExtra("phonenumber",phone);
                setResult(0,d);
                finish();
            }
        });

    }

    public class MyAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {
            //先判断是否有缓存
            ViewHolder holder=null;
            if (convertView == null) {
                convertView = android.view.View.inflate(getApplicationContext(), R.layout.item_contacts, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_contacts_item_name);
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_contacts_item_phone);

                //设置
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(datas.get(position).getName());
            holder.tv_phone.setText(datas.get(position).getPhone());
            return convertView;
        }
    }

    private class ViewHolder{
        TextView tv_name;
        TextView tv_phone;
    }
}
