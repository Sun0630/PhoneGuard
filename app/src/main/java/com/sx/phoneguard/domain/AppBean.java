package com.sx.phoneguard.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by ad on 2016/3/7.
 * 软件的属性封装
 */
public class AppBean {
    private Drawable icon;
    private String name;
    private long size;
    private String path;//软件的路径
    private boolean isRom;//是否是安装在手机内存中
    private boolean isSystem;//是否是系统软件
    private String packageName;//包名

    public int hasCode(){
        return packageName.hashCode();
    }

    public boolean equals(Object o){
        if (o instanceof AppBean){
            AppBean ab = (AppBean) o;
            return packageName.equals(ab.packageName);
        }
        return false;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
