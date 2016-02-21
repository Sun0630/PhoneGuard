package com.sx.phoneguard.domain;

/**
 * Created by ad on 2016/2/20.
 */
public class BlackNumberData {
    public static final int SMS = 1;
    public static final int PHONE = 2;
    public static final int ALL = 3;
    public static final String TABLENAME = "blackname_tb";
    public static final String BLACKNUMBER = "blacknumber";
    public static final String MODE = "mode";
    public static final int PERPAGE = 20;


    /**
     * 黑名单电话号码
     */
    private String blackNumber;
    /**
     * 模式有4种
     * 0：不拦截     00
     * 1：短信拦截   01
     * 2：电话拦截   10
     * 3：全部拦截   11
     */
    private int mode;

    public BlackNumberData(String blackNumber, int mode) {
        this.blackNumber = blackNumber;
        this.mode = mode;
    }

    public String getBlackNumber() {
        return blackNumber;
    }

    public void setBlackNumber(String blackNumber) {
        this.blackNumber = blackNumber;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumberData{" +
                "blackNumber='" + blackNumber + '\'' +
                ", mode=" + mode +
                '}';
    }
}
