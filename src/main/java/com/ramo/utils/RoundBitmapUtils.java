package com.ramo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class RoundBitmapUtils {
    private static final int STROKE_WIDTH = 4;

    public static Bitmap RtoRoundBitmap(Context context, int rId, int borderColor) {
        Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), rId));

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth;
        int borderwidth = width;

        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;

        newWidth = width <= height ? width : height;
        roundPx = newWidth / 2;

        top = dst_left = dst_top = 0;

        bottom = width = newWidth;
        dst_right = dst_bottom = right = newWidth;

        if (width <= height) {
            left = 0;

        } else {
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        drawBorder(borderColor, borderwidth, canvas, paint);

        return output;
    }

    private static void drawBorder(int borderColor, int width, Canvas canvas, Paint paint) {
        //画边框圆圈
        paint.reset();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH + 1);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2, paint);
    }


}
