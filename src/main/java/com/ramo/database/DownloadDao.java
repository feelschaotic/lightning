package com.ramo.database;

import android.database.sqlite.SQLiteDatabase;

import com.ramo.bean.Download;

import java.util.List;

/**
 * Created by ramo on 2016/4/28.
 */
public interface DownloadDao {
    void beginTran(Download download, String handle);

    void deleteDownload(SQLiteDatabase db, Download download);

    void updateDownload(SQLiteDatabase db, Download download) throws Exception;

    List<Download> findDownload(String select, String[] selectValues);

    boolean isExists(Download download);
}
