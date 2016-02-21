package com.sx.phoneguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sx.phoneguard.db.BlackNameDB;
import com.sx.phoneguard.domain.BlackNumberData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ad on 2016/2/20.
 */
public class BlackNumberDao {
    private BlackNameDB mydb;//黑名单数据库

    public BlackNumberDao(Context context) {
        mydb = new BlackNameDB(context);
    }

    /**
     * @param data
     * @return -1 表示添加数据不成功
     */
    public int add(BlackNumberData data) {
        int res = 0;
        SQLiteDatabase db = mydb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackNumberData.BLACKNUMBER, data.getBlackNumber());
        values.put(BlackNumberData.MODE, data.getMode());
        res = (int) db.insert(BlackNumberData.TABLENAME, null, values);
        db.close();
        return res;
    }

    /**
     * 删除记录
     * 需求：
     * 1，记录删除掉
     * 2，修改mode
     */
    public boolean remove(String blackNumber) {
        boolean res = false;
        SQLiteDatabase db = mydb.getWritableDatabase();
        //返回受影响的行数
        int rowNumber = db.delete(BlackNumberData.TABLENAME, BlackNumberData.BLACKNUMBER + "=?", new String[]{blackNumber});
        if(rowNumber != 0){
            res = true;
        }
        db.close();
        return res;
    }

    public boolean remove(BlackNumberData data) {
        return remove(data.getBlackNumber());
    }

    /**
     * 修改模式
     * @param blackBumber
     * @param mode
     * @return
     */
   public boolean update(String blackBumber,int mode){
       boolean res = false;
       SQLiteDatabase db = mydb.getWritableDatabase();
       ContentValues values = new ContentValues();
       values.put(BlackNumberData.MODE, mode);
       int rowNumber = db.update(BlackNumberData.TABLENAME, values, BlackNumberData.BLACKNUMBER + "=?", new String[]{blackBumber});
       if(rowNumber !=0){//修改数据成功
           res = true;
       }
       db.close();
       return res;
   }

    public boolean update(BlackNumberData data){
     return update(data.getBlackNumber(),data.getMode());
    }

    /**
     * 查询所有数据
     * @return
     */

    public List<BlackNumberData> findAll(){
        SQLiteDatabase db = mydb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + BlackNumberData.BLACKNUMBER + "," + BlackNumberData.MODE + " from " + BlackNumberData.TABLENAME, null);
        List<BlackNumberData> datas = new ArrayList<>();
        while (cursor.moveToNext()){
            int mode = cursor.getInt(1);
            String blackNumber = cursor.getString(0);
            BlackNumberData data = new BlackNumberData(blackNumber,mode);
            datas.add(data);
        }
        db.close();
        return datas;
    }

}
