package com.ramo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ramo.bean.File;
import com.ramo.bean.HistoricalRecord;
import com.ramo.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ramo on 2016/4/3.
 */
public class RecordDaoImpl implements RecordDao {
    private MyDatabaseHelper helper;
    private final static String FILE_TABLE_NAME = "record";
    private FileDao fileDaoImpl;

    public RecordDaoImpl(Context context) {
        helper = new MyDatabaseHelper(context);
        fileDaoImpl = new FileDaoImpl(context);
    }

    @Override
    public void beginTran(HistoricalRecord historicalRecord, String handle) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            if (DB.DELETE.equals(handle))
                deleteRecord(db, historicalRecord);
            else if (DB.UPDATE.equals(handle))
                updateRecord(db, historicalRecord);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    public void addRecord(HistoricalRecord record, File file) {
        SQLiteDatabase db = null;

        try {
            db = helper.getWritableDatabase();
            if (file != null)
                fileDaoImpl.addFile(db, file);
            ContentValues values = beanToContentValues(record);
            db.insert(FILE_TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();

        }

    }

    @Override
    public void deleteRecord(SQLiteDatabase db, HistoricalRecord record) {
        db.delete(FILE_TABLE_NAME, "recordId=?", new String[]{record.getRecordId()});
    }

    @Override
    public void updateRecord(SQLiteDatabase db, HistoricalRecord record) throws Exception {
        ContentValues values = beanToContentValues(record);
        db.update(FILE_TABLE_NAME, values, "recordId=?", new String[]{record.getRecordId()});
    }

    @Override
    public List<HistoricalRecord> findAllRecord() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(FILE_TABLE_NAME, null, null, null, null, null, "date");
        return parseCursor(cursor);
    }


    @Override
    public List<HistoricalRecord> findRecord(String select, String[] selectValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(FILE_TABLE_NAME, null, select, selectValues, null, null, null);
        return parseCursor(cursor);
    }

    private ContentValues beanToContentValues(HistoricalRecord record) throws Exception {
        ContentValues values = new ContentValues();
        values.put("recordId", UUID.randomUUID().toString());
        values.put("sender", record.getSender());
        values.put("receiver", record.getReceiver());
        values.put("type", record.getType().toString());
        values.put("date", record.getDate().toString());
        values.put("fileUrl", record.getFile().getFileUrl());
        return values;
    }

    private List<HistoricalRecord> parseCursor(Cursor cursor) {
        List<HistoricalRecord> recordList = new ArrayList<HistoricalRecord>();
        HistoricalRecord record;
        if (cursor.moveToFirst()) {
            do {
                record = new HistoricalRecord();

                record.setRecordId(cursor.getString(cursor.getColumnIndex("recordId")));
                record.setSender(cursor.getString(cursor.getColumnIndex("sender")));
                record.setReceiver(cursor.getString(cursor.getColumnIndex("receiver")));

                String type = cursor.getString(cursor.getColumnIndex("type"));
                record.setType(type.equals(HistoricalRecord.Type.INCOME.toString()) ? HistoricalRecord.Type.INCOME : HistoricalRecord.Type.OUTCOME);
                record.setDate(DateUtils.strToDate(cursor.getString(cursor.getColumnIndex("date"))));

                String fileUrl = cursor.getString(cursor.getColumnIndex("fileUrl"));
                List<File> fileList = fileDaoImpl.findFile("fileUrl=?", new String[]{fileUrl});
             //   L.e("fileUrl:" + fileUrl + "  fileList:" + fileList.toString());
                if (fileList != null && fileList.size() > 0)
                    record.setFile(fileList.get(0));

                recordList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recordList;
    }
}
