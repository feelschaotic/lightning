package com.ramo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ramo.bean.File;
import com.ramo.utils.ImageManageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/4/3.
 */
public class FileDaoImpl implements FileDao {
    private MyDatabaseHelper helper;
    private final static String FILE_TABLE_NAME = "file";


    public FileDaoImpl(Context context) {
        helper = new MyDatabaseHelper(context);
    }

    @Override
    public void beginTran(File file, String handle) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            if (DB.ADD.equals(handle)) {
                addFile(db, file);
            } else if (DB.DELETE.equals(handle))
                deleteFile(db, file);
            else if (DB.UPDATE.equals(handle))
                updateFile(db, file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    public synchronized void addFile(SQLiteDatabase db, File file) {
        if (!isExists(file)) {
            ContentValues values = null;
            try {
                values = beanToContentValues(file);
                db.insert(FILE_TABLE_NAME, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public synchronized void deleteFile(SQLiteDatabase db, File file) {
        db.delete(FILE_TABLE_NAME, "fileUrl=?", new String[]{file.getFileUrl()});
    }

    @Override
    public synchronized void updateFile(SQLiteDatabase db, File file) throws Exception {
        ContentValues values = beanToContentValues(file);
        db.update(FILE_TABLE_NAME, values, "fileUrl=?", new String[]{file.getFileUrl()});
    }

    @Override
    public List<File> findAllFile() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(FILE_TABLE_NAME, null, null, null, null, null, null);
        return parseCursor(cursor);
    }


    @Override
    public List<File> findFile(String select, String[] selectValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(FILE_TABLE_NAME, null, select, selectValues, null, null, null);
        return parseCursor(cursor);
    }

    private ContentValues beanToContentValues(File file) throws Exception {
        ContentValues values = new ContentValues();
        values.put("fileUrl", file.getFileUrl());
        values.put("fileName", file.getFileName());
        values.put("fileSize", file.getFileSize());
        values.put("fileImg", file.getFileImg());
        return values;
    }

    private List<File> parseCursor(Cursor cursor) {
        List<File> fileList = new ArrayList<File>();
        File file;
        if (cursor.moveToFirst()) {
            do {
                file = new File();

                file.setFileUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
                file.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                file.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
                byte[] fileImg = cursor.getBlob(cursor.getColumnIndex("fileImg"));
                Bitmap bitmap = null;
                if (fileImg != null)
                    bitmap = BitmapFactory.decodeByteArray(fileImg, 0, fileImg.length);
                if (bitmap != null)
                    file.setFileImg(ImageManageUtil.bitmap2Byte(bitmap));
                fileList.add(file);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fileList;
    }

    public boolean isExists(File f) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from file where fileUrl = ? ",
                new String[]{f.getFileUrl()});
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }
}
