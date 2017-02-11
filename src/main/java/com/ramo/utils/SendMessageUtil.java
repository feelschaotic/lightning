package com.ramo.utils;

import android.telephony.SmsManager;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by ramo on 2016/7/20.
 */
public class SendMessageUtil {
    private final static String SmsContent="您的通讯录好友向您发送了文件，请登录 www.lightning.com" +
            "接收文件";
    public static void sendSMS(String number) {

        if (!TextUtils.isEmpty(SmsContent) && !TextUtils.isEmpty(number)) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> list = manager.divideMessage(SmsContent);
            for (String content : list) {
                manager.sendTextMessage(number, null, content, null, null);
            }
        }
    }
}
