package com.ramo.bean;

import android.os.Parcel;

/**
 * Created by ramo on 2016/4/5.
 */
public class Multimedia extends File {
    private String multimediaLength;

    public Multimedia() {
        super();
    }

    protected Multimedia(Parcel in) {
        super(in);
        multimediaLength = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        super.writeToParcel(dest, flags);
        dest.writeString(multimediaLength);
    }

    public String getMultimediaLength() {
        return multimediaLength;
    }

    public void setMultimediaLength(String multimediaLength) {
        this.multimediaLength = multimediaLength;
    }
}
