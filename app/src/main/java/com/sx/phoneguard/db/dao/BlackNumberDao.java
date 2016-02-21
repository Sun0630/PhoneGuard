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
 *   黑名单数据库
 *   DAO层
 */
public class BlackNumberDao {
    private BlackNameDB mydb;//黑名单数据库

    public BlackNumberDao(Context context) {
        mydb = new BlackNameDB(context);
    }

    /**
     * 插入记录
     * @param data 需要添加的一个对象
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
        if (rowNumber != 0) {
            res = true;
        }
        db.close();
        return res;
    }

    public boolean remove(BlackNumberData data) {
        return remove(data.getBlackNumber());
    }

    /**
     * 把页码传递过来，然后取到当前页的数据
     * @param pageIndex
     *          第几页
     * @return
     *          当前页的数据
     */
    public List<BlackNumberData> getPageData(int pageIndex){
        SQLiteDatabase db = mydb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + BlackNumberData.BLACKNUMBER + "," + BlackNumberData.MODE + " from "
                + BlackNumberData.TABLENAME +" limit ?,"+ BlackNumberData.PERPAGE,
                new String[]{(pageIndex - 1) * BlackNumberData.PERPAGE + ""});
        List<BlackNumberData> datas = new ArrayList<>();
        while (cursor.moveToNext()) {
            int mode = cursor.getInt(1);
            String blackNumber = cursor.getString(0);
            BlackNumberData data = new BlackNumberData(blackNumber, mode);
            datas.add(data);
        }
        cursor.close();
        db.close();
        return datas;
    }

    /**
     * 分批查询
     * @param startIndex
     *          从哪里开始查
     * @param numbers
     *          一共查询多少条目
     * @return
     *          查询到的数据
     */
    public List<BlackNumberData> getData(String startIndex,String numbers){
        SQLiteDatabase db = mydb.getReadableDatabase();
        //查询出来的数据按照id降序排序
        Cursor cursor = db.rawQuery("select " + BlackNumberData.BLACKNUMBER + "," + BlackNumberData.MODE + " from " + BlackNumberData.TABLENAME
                +" order by _id desc limit ?,?", new String[]{startIndex,numbers});
        List<BlackNumberData> datas = new ArrayList<>();
        while (cursor.moveToNext()) {
            int mode = cursor.getInt(1);
            String blackNumber = cursor.getString(0);
            BlackNumberData data = new BlackNumberData(blackNumber, mode);
            datas.add(data);
        }
        cursor.close();
        db.close();
        return datas;
    }


    /**
     * 得到页数
     * @return
     *      页数
     */
   public int getPages(){
                //效果  得到5.1 即返回 6
       // ceil: 返回不小于当前数的最小整数
       return (int) Math.ceil(getTotal()*1.0 / BlackNumberData.PERPAGE);
   }

    /**
     * 获取数据的总条目数
     */
    public int getTotal() {
        SQLiteDatabase db = mydb.getReadableDatabase();
        //                         下面这条语句比select count(*) form ...执行效率高
        Cursor cursor = db.rawQuery("select count(1) from " + BlackNumberData.TABLENAME, null);
        cursor.moveToNext();
        int raws = cursor.getInt(0);//返回的是该表中的总条目数
        cursor.close();
        db.close();
        return raws;
    }

    /**
     * 修改模式
     *
     * @param blackNumber
     *          要更新的数据
     * @param mode
     *          更新的模式
     * @return
     *          跟新是否成功
     */
    public boolean update(String blackNumber, int mode) {
        boolean res = false;
        SQLiteDatabase db = mydb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackNumberData.MODE, mode);
        int rowNumber = db.update(BlackNumberData.TABLENAME, values, BlackNumberData.BLACKNUMBER + "=?",
                new String[]{blackNumber});
        if (rowNumber != 0) {//修改数据成功
            res = true;
        }
        db.close();
        return res;
    }

    public boolean update(BlackNumberData data) {
        return update(data.getBlackNumber(), data.getMode());
    }

    /**
     * 查询所有数据
     *
     * @return
     *      查询到的数据，封装在集合中
     */

    public List<BlackNumberData> findAll() {
        SQLiteDatabase db = mydb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + BlackNumberData.BLACKNUMBER + "," + BlackNumberData.MODE + " from " + BlackNumberData.TABLENAME, null);
        List<BlackNumberData> datas = new ArrayList<>();
        while (cursor.moveToNext()) {
            int mode = cursor.getInt(1);
            String blackNumber = cursor.getString(0);
            BlackNumberData data = new BlackNumberData(blackNumber, mode);
            datas.add(data);
        }
        cursor.close();
        db.close();
        return datas;
    }

}
