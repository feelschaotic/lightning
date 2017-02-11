package com.ramo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ramo.bean.Download;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/4/3.
 */
public class DownloadDaoImpl implements DownloadDao {
    private MyDatabaseHelper helper;
    private final static String FILE_TABLE_NAME = "download_info";


    public DownloadDaoImpl(Context context) {
        helper = new MyDatabaseHelper(context);
    }

    @Override
    public void beginTran(Download download, String handle) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            if (DB.ADD.equals(handle))
                addDownload(db, download);
            else if (DB.DELETE.equals(handle))
                deleteDownload(db, download);
            else if (DB.UPDATE.equals(handle))
                updateDownload(db, download);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

    }

    private void addDownload(SQLiteDatabase db, Download download) throws Exception {
        ContentValues values = beanToContentValues(download);
        db.insert(FILE_TABLE_NAME, null, values);
    }

    @Override
    public void deleteDownload(SQLiteDatabase db, Download download) {
        db.delete(FILE_TABLE_NAME, "fileUrl = ?", new String[]{download.getFileUrl()});
    }

    @Override
    public void updateDownload(SQLiteDatabase db, Download download) throws Exception {
        db.execSQL("update download_info set compeleteSize = ? where fileUrl = ? and threadId = ?",
                new Object[]{download.getCompeleteSize(), download.getFileUrl(), download.getThreadId()});
    }


    @Override
    public List<Download> findDownload(String select, String[] selectValues) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(FILE_TABLE_NAME, null, select, selectValues, null, null, null);
        return parseCursor(cursor);
    }

    private ContentValues beanToContentValues(Download download) throws Exception {
        ContentValues values = new ContentValues();
        values.put("downloadId", download.getDownloadId());
        values.put("threadId", download.getThreadId());
        values.put("startPos", download.getStartPos());
        values.put("endPos", download.getEndPos());
        values.put("compeleteSize", download.getCompeleteSize());
        values.put("fileUrl", download.getFileUrl());
        values.put("state", download.getState());

        return values;
    }

    private List<Download> parseCursor(Cursor cursor) {
        List<Download> downloadList = new ArrayList<Download>();
        Download download;
        if (cursor.moveToFirst()) {
            do {
                download = new Download();

                download.setDownloadId(cursor.getString(cursor.getColumnIndex("downloadId")));
                download.setThreadId(cursor.getInt(cursor.getColumnIndex("threadId")));
                download.setStartPos(cursor.getInt(cursor.getColumnIndex("startPos")));
                download.setEndPos(cursor.getInt(cursor.getColumnIndex("endPos")));
                download.setCompeleteSize(cursor.getLong(cursor.getColumnIndex("compeleteSize")));
                download.setFileUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
                download.setState(cursor.getInt(cursor.getColumnIndex("state")));

                downloadList.add(download);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return downloadList;
    }

    @Override
    public boolean isExists(Download download) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from download_info where threadId = ? and fileUrl = ?",
                new String[]{download.getThreadId() + "", download.getFileUrl()});
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }
}
