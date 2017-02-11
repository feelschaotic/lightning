package com.ramo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ramo on 2016/4/4.
 */
public class DateUtils {


    public static Date strToDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
        //Mon Apr 04 15:16:15 GMT+0800 2016
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            try {
                return formatter2.parse(str);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }
}
