package com.ramo.utils;

import java.util.Date;

/**
 * Created by ramo on 2016/4/9.
 */
public class DateUtil {

    /**
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
   /* public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " days " + hours + " hours " + minutes + " minutes "
                + seconds + " seconds ";
    }*/
    public static String formatDuring(long mss) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String minutesStr = String.valueOf(minutes);
        String hoursStr = String.valueOf(hours);
        String secondsStr = String.valueOf(seconds);
        if (minutes < 10)
            minutesStr = "0" + minutesStr;
        if (hours < 10)
            hoursStr = "0" + hoursStr;
        if (seconds < 10)
            secondsStr = "0" + secondsStr;

        return hoursStr + ":" + minutesStr + ":"
                + secondsStr;
    }

    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }
}
