package com.sx.phoneguard.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by ad on 2016/3/18.
 *
 */
public class TaskBean {
    private String name;//app的名字
    private String packName;//包名
    private long size;//大小
    private boolean isSystem;//是否是系统进程
    private boolean isChecked;//是否被选择
    private Drawable icon;//应用程序的图标

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
