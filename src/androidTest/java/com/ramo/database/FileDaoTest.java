package com.ramo.database;

import android.test.InstrumentationTestCase;

import com.ramo.application.MyApplication;
import com.ramo.bean.File;

/**
 * Created by ramo on 2016/4/4.
 */
public class FileDaoTest extends InstrumentationTestCase {


    public void testBeginTran() throws Exception {
        FileDaoImpl db = new FileDaoImpl(MyApplication.getContext());

        File file = new File();
        file.setFileUrl("test111");
        file.setFileSize(12000);
        file.setFileName("测试文件.apk");
        file.setFileImg(null);
        db.beginTran(file, "add");
    }

    public void testDeleteFile() throws Exception {

    }

    public void testUpdateFile() throws Exception {

    }

    public void testFindAllFile() throws Exception {

    }

    public void testFindFile() throws Exception {

    }
}