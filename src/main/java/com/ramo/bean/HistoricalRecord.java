package com.ramo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class HistoricalRecord implements Cloneable, Parcelable {

    private String recordId;
    private String sender;
    private String receiver;
    private Type type;
    private Date date;
    private File file;
    private int state = State.FINISH;


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public enum Type {
        INCOME, OUTCOME
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recordId);
        dest.writeString(this.sender);
        dest.writeString(this.receiver);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeParcelable(this.file, flags);
        dest.writeInt(this.state);
    }

    public HistoricalRecord() {
    }

    protected HistoricalRecord(Parcel in) {
        this.recordId = in.readString();
        this.sender = in.readString();
        this.receiver = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.file = in.readParcelable(File.class.getClassLoader());
        this.state = in.readInt();
    }

    public static final Parcelable.Creator<HistoricalRecord> CREATOR = new Parcelable.Creator<HistoricalRecord>() {
        @Override
        public HistoricalRecord createFromParcel(Parcel source) {
            return new HistoricalRecord(source);
        }

        @Override
        public HistoricalRecord[] newArray(int size) {
            return new HistoricalRecord[size];
        }
    };
}
