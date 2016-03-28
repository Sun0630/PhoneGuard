package com.sx.phoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ad on 2016/3/21.
 * 程序锁
 */
public class LockDB extends SQLiteOpenHelper {

    public static final String TABLENAME= "locks";
    public static final String PACKNAME= "packname";
    public static final String URI = "content://com.sx.lockdao";


    public LockDB(Context context) {
        super(context, "lock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLENAME+"(_id integer primary key autoincrement,"+ PACKNAME
        +" text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("delete from "+ TABLENAME);
    }
}
