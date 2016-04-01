package com.sx.phoneguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sx.phoneguard.domain.VirusBean;
import com.sx.phoneguard.utils.Md5Utils;

import java.io.File;
import java.util.List;

/**
 * Created by ad on 2016/3/29.
 * 病毒库
 */
public class VirusDao {

    /**
     *
     * @param context 上下文
     * @return 获取病毒库的版本号
     */
    public static int getVersion(Context context){
        int version = 0;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir() +
                "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        //select desc from datable where md5="92ee1ffff4987bec15108151f73dfe33";
        Cursor cursor = db.rawQuery("select subcnt from version ", null);
        if (cursor.moveToNext()){//说明查到了数据
            version = cursor.getInt(0);
        }
        db.close();
        cursor.close();


        return version;
    }

    /**
     * 保存新的病毒到数据库
     * @param context 上下文
     * @param bean 病毒的bean
     */
    public static void addVirus(Context context, VirusBean bean){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir() +
                "/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5",bean.getMd5());
        values.put("desc",bean.getDesc());
        values.put("type",bean.getType());
        values.put("name",bean.getName());

        db.insert("datable", null, values);
        db.close();
    }

    /**
     * 保存新的多个病毒到数据库
     * @param context 上下文
     * @param beans 病毒的bean
     */
    public static void addAllVirus(Context context, List<VirusBean> beans){
        for (VirusBean b :
                beans) {
            addVirus(context, b);
        }
    }


    /**
     * @param file 要检测的文件
     * @return 文件是否有病毒， null 就是没有病毒
     */
    public static String isVirus(Context context, File file) {
        String res = null;

        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir() +
                "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        //select desc from datable where md5="92ee1ffff4987bec15108151f73dfe33";
        Cursor cursor = db.rawQuery("select desc from datable where md5 = ? ",
                new String[]{Md5Utils.getFileMd5(file)});
        if (cursor.moveToNext()){//说明查到了数据
            res = cursor.getString(0);
        }
        db.close();
        cursor.close();
        return res;
    }
}
