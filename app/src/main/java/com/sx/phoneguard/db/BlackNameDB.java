package com.sx.phoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ad on 2016/2/20.
 */
public class BlackNameDB extends SQLiteOpenHelper {

    public BlackNameDB(Context context) {
        super(context, "blackname.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blackname_tb(_id integer primary key autoincrement,blacknumber text,mode integer)");
    }

    /**
     * 版本号变更的时候调用此方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("delete from blackname_tb");
    }
}
