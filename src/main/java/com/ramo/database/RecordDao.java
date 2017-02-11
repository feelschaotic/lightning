package com.ramo.database;

import android.database.sqlite.SQLiteDatabase;

import com.ramo.bean.File;
import com.ramo.bean.HistoricalRecord;

import java.util.List;

/**
 * Created by ramo on 2016/4/28.
 */
public interface RecordDao {
    void beginTran(HistoricalRecord historicalRecord, String handle);

    void deleteRecord(SQLiteDatabase db, HistoricalRecord record);

    void updateRecord(SQLiteDatabase db, HistoricalRecord record) throws Exception;

    void addRecord(HistoricalRecord record, File file);

    List<HistoricalRecord> findAllRecord();

    List<HistoricalRecord> findRecord(String select, String[] selectValues);
}
