package com.sx.phoneguard.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ad on 2016/2/22.
 */
public class CommunicationLogEngine {
    /**
     *
     * @param context
     *  上下文
     * @return
     * 得到通话记录的电话号码
     */
    public static List<String> getCallLog(Context context){
        List<String> numbers = new ArrayList<>();
        //使用内容提供者查询联系人数据库，得到联系人的信息
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = cr.query(uri, new String[]{"number"}, null, null, null);
        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            //判断相同的记录就不要了
            if(!numbers.contains(number)){
                numbers.add(number);
            }

        }
        cursor.close();
        return numbers;
    }

    /**
     *
     * @param context
     *  上下文
     * @return
     *  短信数据的地址
     */
    public static List<String> getSMSLog(Context context){
        List<String> numbers = new ArrayList<>();
        //使用内容提供者查询联系人数据库，得到联系人的信息
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = cr.query(uri, new String[]{"address"}, null, null, null);
        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            //判断相同的记录就不要了
            if(!numbers.contains(number)){
                numbers.add(number);
            }

        }
        cursor.close();
        return numbers;
    }
}
