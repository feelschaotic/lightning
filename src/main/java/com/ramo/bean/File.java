package com.ramo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by ramo on 2016/4/3.
 */
public class File implements Parcelable {
    private String fileUrl;//url为文件的id
    private String fileName;
    private String fileRealName;
    private long fileSize;
    private byte[] fileImg;
    private Date date;
    private FileType type;
    private int finished;
    private int flag;

    public  File(){
        super();
    }
    protected File(Parcel in) {

        fileUrl = in.readString();
        fileName = in.readString();
        fileRealName = in.readString();
        fileSize = in.readLong();
        fileImg = in.createByteArray();
        date= (Date) in.readSerializable();
        type= (FileType) in.readSerializable();
        finished = in.readInt();
        flag = in.readInt();
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };

    public String getFileRealName() {
        return fileRealName;
    }

    public void setFileRealName(String fileRealName) {
        this.fileRealName = fileRealName;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileUrl);
        dest.writeString(fileName);
        dest.writeString(fileRealName);
        dest.writeLong(fileSize);
        dest.writeByteArray(fileImg);
        dest.writeSerializable(date);
        dest.writeSerializable(type);
        dest.writeInt(finished);
        dest.writeInt(flag);
    }

    public enum FileType {
        IMAGE, MEDIO, VIDEO, APP, TEXT
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getFileImg() {
        return fileImg;
    }

    public void setFileImg(byte[] fileImg) {
        this.fileImg = fileImg;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
