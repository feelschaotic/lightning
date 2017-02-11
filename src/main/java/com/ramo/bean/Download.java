package com.ramo.bean;

import java.io.Serializable;

/**
 * Created by ramo on 2016/4/28.
 */
public class Download implements Serializable {
    private String downloadId;
    private int threadId;
    private long startPos;
    private long endPos;
    private long compeleteSize;
    private String fileUrl;
    private int state;
    public Download(){

    }
    public Download(int threadId, String fileUrl, long startPos, long endPos, int state) {
        this.threadId = threadId;
        this.fileUrl = fileUrl;
        this.startPos = startPos;
        this.endPos = endPos;
        this.state = state;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public long getCompeleteSize() {
        return compeleteSize;
    }

    public void setCompeleteSize(long compeleteSize) {
        this.compeleteSize = compeleteSize;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
