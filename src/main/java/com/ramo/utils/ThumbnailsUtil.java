package com.ramo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ramo on 2016/4/5.
 */
public class ThumbnailsUtil {
    public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;

        Bitmap src = BitmapFactory.decodeFile(pathName, options);

        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响

    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {

        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);

        if (src != dst) { // 如果没有缩放，那么不回收

            src.recycle(); // 释放Bitmap的native像素数组
            System.gc();
        }
        return dst;

    }

    public static Bitmap decodeBitmapFromStream(InputStream inputStream, int w, int h) {
      /*  //修改前
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//设置此值不读取图片 只读取图片属性
        BitmapFactory.decodeStream(inputStream,null, options);//直接读取图片会抛出内存泄漏异常
        options.inJustDecodeBounds = false;//一定要设置回来
        options.inSampleSize = calculateInSampleSize(options, w, h);//计算压缩比
        Bitmap src = BitmapFactory.decodeStream(inputStream, null, options);//此时src为空！！！
        *//**
         * 原因：decodeStream inputStream流不能解析两次
         * 处理方法：把输入流转为byte数组保存
         *//*
        return createScaleBitmap(src, w, h);*/
        //修改后
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//设置此值不读取图片 只读取图片属性
        byte[] data = readStream(inputStream);
        BitmapFactory.decodeByteArray(data, 0, data.length, options);//直接读取图片会抛出内存泄漏异常
        options.inJustDecodeBounds = false;//一定要设置回来
        options.inSampleSize = calculateInSampleSize(options, w, h);//计算压缩比
        Bitmap src = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return createScaleBitmap(src, w, h);
    }

    /*
         * 得到图片字节流 数组大小
         * */
    public static byte[] readStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            outStream.close();
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

    public static Bitmap decodeBitmap(Bitmap src, int reqWidth, int reqHeight) {

        return createScaleBitmap(src, reqWidth, reqHeight);
    }
}
