package com.ramo.utils;

import java.util.Random;

/**
 * Created by ramo on 2016/4/10.
 */
public class RandomUtil {
    /**
     * 得到n位长度的随机数
     *
     * @param digits 随机数的长度
     * @return 返回 n位的随机整数
     */
    public static int getRandomNumber(int digits) {
        int temp = 0;
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits);
        Random rand = new Random();

        while (true) {
            temp = rand.nextInt(max);
            if (temp >= min)
                break;
        }

        return temp;

    }
}
