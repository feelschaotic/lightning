package com.ramo.bean;

import android.os.Parcel;

/**
 * Created by ramo on 2016/3/24.
 */
public class AppInfo extends File {
    private String packageName;

    public AppInfo() {
        super();
    }

    protected AppInfo(Parcel in) {
        super(in);
        packageName = in.readString();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(packageName);
    }
}
