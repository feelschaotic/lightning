package com.ramo.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.ramo.application.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class ImageManageUtil {
    public static Drawable RToDrawable(int r) {
        Resources resources = MyApplication.getContext().getResources();
        Drawable drawable = resources.getDrawable(r);
        return drawable;
    }

    public static Bitmap RToBitmap(int r) {
        Resources res = MyApplication.getContext().getResources();
        InputStream is = res.openRawResource(r);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        return bmpDraw.getBitmap();
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    public static Bitmap StrToBitmap(String report_img) {
        byte[] bytes = Base64.decode(report_img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static String drawableToStr(Drawable drawable) {
        Bitmap bitmap = drawableToBitamp(drawable);
        return bitmapToString(bitmap);
    }

    public static byte[] bitmap2Byte(Bitmap b) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 0, baos);// 压缩位图
        return baos.toByteArray();// 创建分配字节数组

    }

    public static Bitmap Byte2Bitmap(byte[] b) {
        return BitmapFactory.decodeByteArray(b, 0, b.length);//从字节数组解码位图
    }

    public static byte[] drawable2Byte(Drawable drawable) {
        return bitmap2Byte(drawableToBitamp(drawable));
    }

    public static Drawable Byte2Drawable(byte[] b) {
        return new BitmapDrawable(Byte2Bitmap(b));

    }


}
