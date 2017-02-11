package com.ramo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ramo.utils.L;

/**
 * Created by ramo on 2016/4/3.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_RECORD = "create table record ("
            + "recordId varchar(40) primary key,"
            + "sender varchar(40) ,"
            + "receiver varchar(40) ,"
            + "type varchar(10) not null,"
            + "fileUrl varchar(100) not null,"
            + "date time,"
            + "FOREIGN KEY (fileUrl) REFERENCES file(fileUrl)"
            + ")";
    public static final String CREATE_FILE = "create table file ("
            + "fileUrl varchar(100) primary key,"
            + "fileName varchar(100) ,"
            + "fileSize varchar(10) ,"
            + "fileImg blob"
            + ")";
    public static final String CREATE_USER = "create table user ("
            + "userId varchar(40) primary key,"
            + "userName varchar(50) ,"
            + "userHead blob "
            + ")";
    public static final String CREATE_DOWNLOAD = "create table download_info ("
            + "downloadId varchar(40) primary key,"
            + "threadId int ,"
            + "startPos long ,"
            + "endPos long ,"
            + "compeleteSize long,"
            + "fileUrl varchar(100),"
            + "state int,"
            + "FOREIGN KEY (fileUrl) REFERENCES file(fileUrl)"
            + ")";

    public static final String DROP_RECORD = "drop table if exists record";
    public static final String DROP_DOWNLOAD = "drop table if exists download_info";
    public static final String DROP_FILE = "drop table if exists file";
    public static final String DROP_USER = "drop table if exists user";


    public MyDatabaseHelper(Context context) {
        super(context, DB.DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        L.e("SQLite 数据库创建！！！");
        create(sqLiteDatabase);
    }

    private void create(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER);
        sqLiteDatabase.execSQL(CREATE_FILE);
        sqLiteDatabase.execSQL(CREATE_DOWNLOAD);
        sqLiteDatabase.execSQL(CREATE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        drop(sqLiteDatabase);
        create(sqLiteDatabase);
    }

    private void drop(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DROP_RECORD);
        sqLiteDatabase.execSQL(DROP_DOWNLOAD);
        sqLiteDatabase.execSQL(DROP_FILE);
        sqLiteDatabase.execSQL(DROP_USER);
    }
}
