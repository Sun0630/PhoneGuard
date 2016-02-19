package com.sx.phoneguard.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sx.phoneguard.R;

/**
 * Created by ad on 2016/2/19.
 */
public class SettingView extends LinearLayout {

    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_checked;
    private String settingTitle;
    private String content;
    private String[] desc;
    private View rl_root;

    public SettingView(Context context) {
        super(context);
        init();
    }

    /**
     * 会调用的构造函数
     *
     * @param context
     * @param attrs
     */
    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        settingTitle = attrs.getAttributeValue("http://schemas.android.com/apk/com.sx.phoneguard", "settingTitle");
        content = attrs.getAttributeValue("http://schemas.android.com/apk/com.sx.phoneguard", "content");
        desc = content.split(",");
        initData();

    }

    private void initData() {
        tv_title.setText(settingTitle);
        tv_content.setTextColor(Color.RED);
        tv_content.setText(desc[1]);
    }

    /**
     * 设置自定义View的状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        if (checked) {
            tv_content.setTextColor(Color.GREEN);
            tv_content.setText(desc[0]);
            cb_checked.setChecked(checked);
        } else {
            tv_content.setTextColor(Color.RED);
            tv_content.setText(desc[1]);
        }
        cb_checked.setChecked(checked);
    }

    public boolean getChecked() {
        return cb_checked.isChecked();
    }

    /**
     * 接收这个相对布局的点击事件
     *
     * @param listener
     */
    public void setClickListener(OnClickListener listener) {
        rl_root.setOnClickListener(listener);
    }

    private void init() {
        //第一句的功能等于后两句                                                   this就代表父组件
        //      View view = View.inflate(getContext(), R.layout.item_setting_view,this);
        //这个view就是RL的根布局，把自定义的View添加到父组件容器中。
        rl_root = View.inflate(getContext(), R.layout.item_setting_view, null);
        addView(rl_root);
        tv_title = (TextView) rl_root.findViewById(R.id.tv_setting_item_title);
        tv_content = (TextView) rl_root.findViewById(R.id.tv_setting_item_content);
        cb_checked = (CheckBox) rl_root.findViewById(R.id.cb_setting_checked);
    }
}
