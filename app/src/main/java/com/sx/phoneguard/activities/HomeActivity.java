package com.sx.phoneguard.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.Md5Utils;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;

public class HomeActivity extends AppCompatActivity {
    private StartAppAd startAppAd = new StartAppAd(this);
    private GridView gv_jiugongge;
    private GVAdapter adapter;
    private SharedPreferences sp;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.SPNAME, MODE_PRIVATE);
        StartAppSDK.init(this, "202885025", "true");
        startAppAd.showAd(); // show the ad
        startAppAd.loadAd(); // load the next ad
        initView();//初始化界面
        initData();//初始化数据
        initEvent();//所有组件的初始化事件
    }

    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
    }


    /**
     * 判断是否设置过密码
     *
     * @return
     */
    private boolean isSetPass() {
        boolean res = false;
        //拿到SharePreference中保存的密码

        String password = sp.getString(MyConstants.PASSWORD, "");
        if (!TextUtils.isEmpty(password)) {//密码不为空
            res = true;
        }
        return res;
    }

    /**
     * 显示输入密码的对话框
     */
    private void showEnterPassDialog() {
        //密码保存到SHarePreference中
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.dialog_inputpassword, null);
        //获取组件
        final EditText et_pass = (EditText) view.findViewById(R.id.et_dialog_pass);
        Button bt_enter = (Button) view.findViewById(R.id.bt_dialog_enter);
        Button bt_cancle = (Button) view.findViewById(R.id.bt_dialog_cancle);

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存密码
                //拿到输入框中的数据先
                String pass = et_pass.getText().toString().trim();
                //校验密码
                if (TextUtils.isEmpty(pass)) {
                    ShowToast.show(HomeActivity.this, "密码不能为空");
                    return;
                }
                if (!Md5Utils.md5JiaMi(Md5Utils.md5JiaMi(pass)).equals(sp.getString(MyConstants.PASSWORD, ""))) {
                    ShowToast.show(HomeActivity.this, "密码错误");
                } else {
                    //进入手机防盗界面
                    loadLostFind();

                    dialog.dismiss();
                }

            }
        });
        builder.setView(view);//在对话框中填充进去一个view对象
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 加载手机防盗界面
     */
    private void loadLostFind() {
        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
        startActivity(intent);
    }

    /**
     * 显示设置密码的对话框
     */
    private void showSetPassDialog() {
        //密码保存到SHarePreference中
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.dialog_serpassword, null);
        //获取组件
        final EditText et_setpass = (EditText) view.findViewById(R.id.et_dialog_setpass);
        final EditText et_setconfirmpass = (EditText) view.findViewById(R.id.et_dialog_setconfirmpass);
        Button bt_setpass = (Button) view.findViewById(R.id.bt_dialog_setpass);
        Button bt_cancle = (Button) view.findViewById(R.id.bt_dialog_cancle);

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_setpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存密码
                //拿到输入框中的数据先
                String pass = et_setpass.getText().toString().trim();
                String confirmpass = et_setconfirmpass.getText().toString().trim();
                //校验密码
                if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmpass)) {
                    ShowToast.show(HomeActivity.this, "密码不能为空");
                    return;
                }
                if (!pass.equals(confirmpass)) {
                    ShowToast.show(HomeActivity.this, "两次密码不一致");
                } else {
                    ShowToast.show(HomeActivity.this, "密码设置成功！");
                    sp.edit().putString(MyConstants.PASSWORD, Md5Utils.md5JiaMi(Md5Utils.md5JiaMi(pass))).commit();
                    dialog.dismiss();
                }

            }
        });
        builder.setView(view);//在对话框中填充进去一个view对象
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    private void initEvent() {
        //为表格布局的每个项都设置监听
        gv_jiugongge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //有9宫格
                switch (position) {
                    case 0://手机防盗
                        /*
                        逻辑：
                            判断有没有设置过密码isSetPass()
                            如果已经设置过了密码，就显示输入密码的对话框showEnterPass();
                             如果没有设置密码，显示设置密码的对话框showSetPass();
                         */
                        if (isSetPass()) {//设置过密码了
                            //显示输入密码的对话框
                            showEnterPassDialog();
                        } else {
                            //显示设置密码对话框
                            showSetPassDialog();
                        }
                        break;
                    case 1://通讯卫士
                        Intent intent1 = new Intent(HomeActivity.this, CommunicationGuardActivity.class);
                        startActivity(intent1);
                        break;
                    case 2://软件管家
                        Intent intent2 = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent2);
                        break;
                    case 3://进程管理
                        Intent intent3 = new Intent(HomeActivity.this, TaskManagerActivity.class);
                        startActivity(intent3);
                        break;
                    case 4://进程管理
                        Intent intent4 = new Intent(HomeActivity.this, TranficActivity.class);
                        startActivity(intent4);
                        break;
                    case 5://手机杀毒
                        Intent intent5 = new Intent(HomeActivity.this, AntiVirusActivity.class);
                        startActivity(intent5);
                        break;
                    case 6://手机杀毒
                        Intent intent6 = new Intent(HomeActivity.this, CacheActivity.class);
                        startActivity(intent6);
                        break;
                    case 7://高级工具
                        Intent intent7 = new Intent(HomeActivity.this, AToolsActivity.class);
                        startActivity(intent7);
                        break;
                    case 8://设置中心
                        Intent intent8 = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent8);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void initData() {
        adapter = new GVAdapter();
        gv_jiugongge.setAdapter(adapter);
    }

    String[] names = {"手机防盗", "通讯卫士", "软件管家",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"};

    int[] icons = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.icon_selector,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings
    };

    private void initView() {
        setContentView(R.layout.activity_home);
        gv_jiugongge = (GridView) findViewById(R.id.gv_home_jiugongge);
    }

    class GVAdapter extends BaseAdapter {

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
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_home_gridview, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_girdview_item_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_gridview_item_name);
                //绑定子组件
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //为这两个组件设置显示内容
            holder.iv_icon.setBackgroundResource(icons[position]);
            holder.tv_name.setText(names[position]);
            if (position == 0) {
                String newName = sp.getString(MyConstants.NEWNAME, "");
                if (!TextUtils.isEmpty(newName)) {
                    holder.tv_name.setText(newName);
                }
            }

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_icon;
            private TextView tv_name;
        }
    }
}
