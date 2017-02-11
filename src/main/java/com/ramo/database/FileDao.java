package com.ramo.database;

import android.database.sqlite.SQLiteDatabase;

import com.ramo.bean.File;

import java.util.List;

/**
 * Created by ramo on 2016/4/28.
 */
public interface FileDao {
    void beginTran(File file, String handle);

    void deleteFile(SQLiteDatabase db, File file);

    void updateFile(SQLiteDatabase db, File file) throws Exception;

    void addFile(SQLiteDatabase db, File file) ;

    List<File> findAllFile();

    List<File> findFile(String select, String[] selectValues);
}
