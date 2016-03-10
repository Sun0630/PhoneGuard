package com.sx.phoneguard.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * 短信工具类
 * 备份短信
 * 还原短信
 * Created by ad on 2016/3/3.
 */
public class SmsUtils {


    /**
     * 定义一个回调接口
     */

    public interface ProgressCallBack{
        void setProgress(int current);
        void setMax(int max);
    }

    /**
     * 短信还原原理：
     *      把之前备份的短信的xml文件解析之后插入到短信数据库中即可
     * @param context
     * 上下文
     * @param pd
     * 回调接口
     */
    public static void smsResume(Context context,ProgressCallBack pd){
        //使用dom4j来解析xml文件
        Uri uri = Uri.parse("content://sms");
        SAXReader reader = new SAXReader();
        try {
            //读取xml文件
            Document doc = reader.read(new File(Environment.getExternalStorageDirectory(), "sms.xml"));
            //得到xml文件的根节点
            Element root = doc.getRootElement();
            List sms = root.elements("sms");
            //拿到元素的迭代器
            Iterator<Element> iterator = sms.iterator();
            pd.setMax(sms.size());
            int number = 0;
            //开始迭代，读取xml文件中的内容
            while (iterator.hasNext()) {
                SystemClock.sleep(100);
                Element smsEle = iterator.next();
                //读取出数据插入到短信数据库中,恢复短信
                ContentValues values = new ContentValues();
                values.put("address",smsEle.elementText("address"));
                values.put("type",smsEle.elementText("type"));
                values.put("date",smsEle.elementText("date"));
                values.put("body", smsEle.elementText("body"));


                Uri insert = context.getContentResolver().insert(uri, values);
                pd.setProgress(++number);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * 备份短信
     *
     * @param context 上下文
     * @param pd
     */
    public static void BakeUpSms(Context context, ProgressDialog pd) {
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "type", "date", "body"}, null, null, null);
        //拿到xml序列化对象
        XmlSerializer newSerializer = Xml.newSerializer();
        //指定文件的保存位置

        File file = new File(Environment.getExternalStorageDirectory(), "sms.xml");
        try {
            newSerializer.setOutput(new FileOutputStream(file), "utf-8");
            newSerializer.startDocument("utf-8", true);
            //生成跟标记
            newSerializer.startTag(null, "smses");
            int count = cursor.getCount();
            //设置进度对话框的最大值
            pd.setMax(count);

            newSerializer.attribute(null, "smses", count + "");
            int number = 0;//备份的条数
            while (cursor.moveToNext()) {
                //取出短信
                newSerializer.startTag(null, "sms");

                //address
                {
                    newSerializer.startTag(null, "address");
                    newSerializer.text(cursor.getString(0));
                    newSerializer.endTag(null, "address");
                }
                //type
                {
                    newSerializer.startTag(null, "type");
                    newSerializer.text(cursor.getString(1));
                    newSerializer.endTag(null, "type");
                }
                //date
                {
                    newSerializer.startTag(null, "date");
                    newSerializer.text(cursor.getString(2));
                    newSerializer.endTag(null, "date");
                }

                //body
                {
                    newSerializer.startTag(null, "body");
                    newSerializer.text(cursor.getString(3));
                    newSerializer.endTag(null, "body");
                }

                //模拟备份时间
                SystemClock.sleep(100);
                newSerializer.endTag(null, "sms");
                pd.setProgress(++number);
                //保存短信
            }//while循环结束之后关闭游标
            cursor.close();
            pd.dismiss();
            newSerializer.endTag(null, "smses");//结束的跟标记
            newSerializer.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void BakeUpSms(Context context, ProgressCallBack pd) {
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "type", "date", "body"}, null, null, null);
        //拿到xml序列化对象
        XmlSerializer newSerializer = Xml.newSerializer();
        //指定文件的保存位置
        File file = new File(Environment.getExternalStorageDirectory(), "sms.xml");
        try {
            newSerializer.setOutput(new FileOutputStream(file), "utf-8");
            newSerializer.startDocument("utf-8", true);
            //生成跟标记
            newSerializer.startTag(null, "smses");
            int count = cursor.getCount();
            //设置进度对话框的最大值
            pd.setMax(count);

            newSerializer.attribute(null, "smses", count + "");
            int number = 0;//备份的条数
            while (cursor.moveToNext()) {
                //取出短信
                newSerializer.startTag(null, "sms");

                //address
                {
                    newSerializer.startTag(null, "address");
                    newSerializer.text(cursor.getString(0));
                    newSerializer.endTag(null, "address");
                }
                //type
                {
                    newSerializer.startTag(null, "type");
                    newSerializer.text(cursor.getString(1));
                    newSerializer.endTag(null, "type");
                }
                //date
                {
                    newSerializer.startTag(null, "date");
                    newSerializer.text(cursor.getString(2));
                    newSerializer.endTag(null, "date");
                }

                //body
                {
                    newSerializer.startTag(null, "body");
                    newSerializer.text(cursor.getString(3));
                    newSerializer.endTag(null, "body");
                }

                //模拟备份时间
                SystemClock.sleep(100);
                newSerializer.endTag(null, "sms");
                pd.setProgress(++number);
                //保存短信
            }//while循环结束之后关闭游标
            cursor.close();
            newSerializer.endTag(null, "smses");//结束的跟标记
            newSerializer.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
