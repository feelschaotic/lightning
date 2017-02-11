package com.ramo.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by ramo on 2016/4/3.
 */
public class User implements Serializable{
    private String userId;
    private Drawable userHead;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Drawable getUserHead() {
        return userHead;
    }

    public void setUserHead(Drawable userHead) {
        this.userHead = userHead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
