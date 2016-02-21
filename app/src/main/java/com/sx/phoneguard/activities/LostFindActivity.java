package com.sx.phoneguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sx.phoneguard.R;
import com.sx.phoneguard.utils.MyConstants;
import com.sx.phoneguard.utils.ShowToast;

public class LostFindActivity extends Activity {


    private SharedPreferences sp;
    private TextView tv_safenumber;
    private ImageView iv_lock;
    private AlertDialog dialog = null;
    private LinearLayout ll_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(MyConstants.SPNAME,MODE_PRIVATE);
        //判断有没有设置过向导，如果设置过了，直接进入手机防盗界面，如果没有设置过，需要进行四步的设置向导步骤
        if(sp.getBoolean(MyConstants.ISSET,false)) {//true设置向导完成。
            initView();//初始化界面
            initData();
        }else {//进入第一个设置向导界面
             //显示第一个向导界面
            startSetUp1();
        }
    }

    /**
     * 菜单项
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//       getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    /**
     * 点击菜单项的事件
     * @param featureId
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.mi_splash_modifyName:
                //修改手机防盗的名字，弹出对话框，输入新的名字，保存到sp中
                showEnterPassDialog();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }



    /**
     * 点击菜单按钮的时候触发的方法。
     * @param featureId
     * @param menu
     * @return
     */
    private boolean isMenuUp=false;
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        isMenuUp = !isMenuUp;
        if(isMenuUp){
            ll_menu.setVisibility(View.VISIBLE);
        }else {
            ll_menu.setVisibility(View.GONE);
        }
        return false;//为true显示用户自定义的菜单，为false显示系统的菜单
    }

    /**
     * 显示输入密码的对话框
     */
    private void showEnterPassDialog(){
        //密码保存到SHarePreference中
        AlertDialog.Builder builder = new AlertDialog.Builder(LostFindActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.dialog_inputnewname, null);
        //获取组件
        final EditText et_newName = (EditText) view.findViewById(R.id.et_dialog_newname);
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
                String newName = et_newName.getText().toString().trim();
                //校验新名称
                if (TextUtils.isEmpty(newName)) {
                    ShowToast.show(LostFindActivity.this, "名称不能为空");
                    return;
                }else {
                    sp.edit().putString(MyConstants.NEWNAME,newName).commit();
                    dialog.dismiss();
                    loadMain();
                }
            }
        });
        builder.setView(view);//在对话框中填充进去一个view对象
        dialog = builder.create();
        dialog.show();
    }

    public void loadMain() {
        Intent intent = new Intent(LostFindActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();//关闭Splash界面
    }
    /**
     * 重新进入设置向导
     */

    public void entersetup(View view){
        startSetUp1();
    }
    private void initData() {
        tv_safenumber.setText(sp.getString(MyConstants.SAFEPHONE,""));
    }

    private void startSetUp1() {
        Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        //使用SharePreference来保存是否设置过向导的数据
        //拿到两个组件
        tv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
        iv_lock = (ImageView) findViewById(R.id.iv_lostfind_lock);
        ll_menu = (LinearLayout) findViewById(R.id.ll_lostfind_menu);
        if(sp.getBoolean(MyConstants.ISLOCK,false)){
            iv_lock.setImageResource(R.drawable.lock);
        }else {
            iv_lock.setImageResource(R.drawable.unlock);
        }
    }
}
