package com.ramo.bean;

import java.io.Serializable;

/**
 * Created by ramo on 2016/8/19.
 */
public class JuniorBean implements Serializable{
    private String junior_name;
    private String junior_address;

    public JuniorBean(String junior_name, String junior_address) {
        this.junior_name = junior_name;
        this.junior_address = junior_address;
    }

    public String getJunior_name() {
        return junior_name;
    }

    public void setJunior_name(String junior_name) {
        this.junior_name = junior_name;
    }

    public String getJunior_address() {
        return junior_address;
    }

    public void setJunior_address(String junior_address) {
        this.junior_address = junior_address;
    }
}
