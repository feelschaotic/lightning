package com.ramo.asynctask;
import android.content.Context;

import com.ramo.bean.Download;
import com.ramo.bean.File;
import com.ramo.database.DB;
import com.ramo.database.DownloadDao;
import com.ramo.database.DownloadDaoImpl;

import java.io.Serializable;
import java.util.List;



/**
 * 下载任务类
 */
public class Task implements Serializable{
    private File mFileInfo = null;
    private long mFinished = 0;
    private DownloadDao mDao = null;
    public boolean isPause = false;
    private int mThreadCount = 1;  // 线程数量
    private  Download download = null;

    /**
     * @param mContext
     * @param mFileInfo
     */
    public Task(Context mContext, File mFileInfo, int count) {
        this.mFileInfo = mFileInfo;
        this.mThreadCount = count;
        mDao = new DownloadDaoImpl(mContext);
    }

    public void readThreadInfoFromDB() {
        // 读取数据库的线程信息
        List<Download> threads = mDao.findDownload("fileUrl=?", new String[]{mFileInfo.getFileUrl()});


        if (0 == threads.size()) {
            download = new Download(0, mFileInfo.getFileUrl(), 0, mFileInfo.getFileSize(), 0);
        } else {
            download = threads.get(0);
        }
        if (!mDao.isExists(download)) {
            mDao.beginTran(download, DB.ADD);
        }
    }

    private synchronized void checkAllThreadFinished() {

        // 删除下载记录
        // 发送广播知道UI下载任务结束
    }

    public String getFileUrl() {
        return mFileInfo.getFileUrl();
    }

    public String getFileRealName() {
        return mFileInfo.getFileRealName();
    }

    public long getFinished() {
        return mFinished;
    }

    public void setFinished(long finished) {
        this.mFinished = finished;
    }

    public void updateProgress() {
        download.setCompeleteSize(mFinished);
        mDao.beginTran(download, DB.UPDATE);
    }

    public long getFileLength() {
        return mFileInfo.getFileSize();
    }

    public void delete() {
        mDao.beginTran(download,DB.DELETE);
    }

    public File getFile() {
        return mFileInfo;
    }
}
