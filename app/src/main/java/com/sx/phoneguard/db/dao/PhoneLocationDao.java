package com.sx.phoneguard.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ad on 2016/2/29.
 * 电话归属地的数据库操作
 */
public class PhoneLocationDao {

    /**
     * 查询手机号码归属地
     *
     * @param context     上下文
     * @param phoneNumber 要查询的电话号码
     * @return 电话号码归属地
     */
    public String find(Context context, String phoneNumber) {
        String address = "";
        //判断格式，如果是手机号
        if (phoneNumber.matches("^1[3-9]{1}[0-9]{9}$")) {//手机号
            address = mobile(context, phoneNumber);
        } else if (phoneNumber.length() == 3) {
            if (phoneNumber.equals("110") ) {
                address = "匪警电话";
            } else if (phoneNumber.equals("120")) {
                address = "急救电话";
            } else if (phoneNumber.equals("119")) {
                address = "火警电话";
            }
        } else if (phoneNumber.length() == 5) {
            if (phoneNumber.equals("95555")) {
                address = "中国工商银行";
            } else if (phoneNumber.equals("95533")) {
                address = "中国银行";
            }
        } else if (phoneNumber.length() == 6) {
            address = "服务号码";
        } else if (phoneNumber.length() >= 7) {
            if (phoneNumber.length() < 9) {
                address = "本地号码";
            } else {//查询区号
                address = guhua(context, phoneNumber);
            }
        }else {
            address = phoneNumber;
        }
        //如果是固定电话

        return address;
    }

    /**
     * 查询手机号码归属地
     *
     * @param context     上下文
     * @param phoneNumber 要查询的电话号码
     * @return 电话号码归属地
     */
    public String mobile(Context context, String phoneNumber) {
        String address = "";
        //打开数据库进行查询
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir() + "/address.db", null, SQLiteDatabase.OPEN_READONLY);
        //只需要手机号的前七位就可以
        Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id = ?)",
                new String[]{phoneNumber.substring(0, 7)});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
        }
        cursor.close();

        return address;
    }

    /**
     * 查询固定电话归属地
     *
     * @param context     上下文
     * @param phoneNumber 要查询的电话号码
     * @return 电话号码归属地
     */
    public String guhua(Context context, String phoneNumber) {
        String address = "区号找不到";
        //打开数据库进行查询
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir() + "/address.db", null, SQLiteDatabase.OPEN_READONLY);
        //需要得到区号
        String quhao = phoneNumber.substring(1, 3);//两位的区号，例如010,021，0自动忽略
        Cursor cursor = db.rawQuery("select location from data2 where area = ?",
                new String[]{quhao});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
        } else {
            quhao = phoneNumber.substring(1, 4);//3位的区号，例如0371
            cursor = db.rawQuery("select location from data2 where area = ?",
                    new String[]{quhao});

            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        }
        cursor.close();

        return address;
    }
}
