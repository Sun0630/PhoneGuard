package com.sx.phoneguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sx.phoneguard.db.LockDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ad on 2016/3/21.
 * 操作程序锁
 */
public class LockDao {

    private LockDB lockdb;

    public LockDao(Context context) {
        this.lockdb = new LockDB(context);
    }

    /**
     * 通过界面添加数据，不可能重复
     *
     * @param packName 包名
     */
    public void add(String packName) {
        SQLiteDatabase db = lockdb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LockDB.PACKNAME, packName);
        db.insert(LockDB.TABLENAME, null, values);
        db.close();
    }

    /**
     * 删除数据
     *
     * @param packName
     */
    public void delete(String packName) {
        SQLiteDatabase db = lockdb.getWritableDatabase();
        db.delete(LockDB.TABLENAME, LockDB.PACKNAME + " = ? ", new String[]{packName});
        db.close();
    }


    /**
     * 判断程序是否加锁
     *
     * @param packName
     * @return
     */
    public boolean isLocked(String packName) {
        boolean res = false;
        SQLiteDatabase db = lockdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select 1 from " + LockDB.TABLENAME + " where " + LockDB.PACKNAME + " = ?"
                , new String[]{packName});
        if (cursor.moveToNext()) {
            return true;
        }
        db.close();
        return res;
    }

    /**
     * @return 获取所有加锁的包的信息
     */
    public List<String> getLockedPacks() {
        List<String> datas = new ArrayList<>();
        SQLiteDatabase db = lockdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + LockDB.PACKNAME + " from " + LockDB.TABLENAME, null);
        while (cursor.moveToFirst()) {
            String data = cursor.getString(0);
            datas.add(data);
        }
        cursor.close();
        db.close();

        return datas;
    }
}
