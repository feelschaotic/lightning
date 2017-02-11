package com.ramo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ramo.file_transfer.R;
import com.ramo.utils.PhoneMemoryUtil;
import com.ramo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/4/6.
 */
public class WPStyleView extends View {
    private int measuredWidth;
    private int measuredHeight;
    private int interval = 8;

    private Paint rectPaint;
    private Paint titleTextPaint;
    private Paint contentTextPaint;

    private int smallRectWidth;
    private int bigRectWidth;
    private int smallRectHeight;
    private int bigRectHeight;


    private String text[] = {"安装包", "文档", "手机内存", "电子书", "压缩包", "大文件"};
    private String content[] = {"0项", "1项", StringUtil.formateFileSize(PhoneMemoryUtil.getAvailableInternalMemorySize()) + "/" + StringUtil.formateFileSize(PhoneMemoryUtil.getTotalInternalMemorySize()), "0项", "0项", "4项"};
    private String color[] = {"#6CC5FD", "#191970", "#4169E1", "#6495ED", "#4169E1", "#6495ED"};
    private int icoRes[] = {R.drawable.count_file_ic_app, R.drawable.doc_lines, R.drawable.count_file_ic_phone,
            R.drawable.count_file_ic_book, R.drawable.count_file_ic_package, R.drawable.count_file_ic_clip};

    private List<Bitmap> icoBitmap = new ArrayList<Bitmap>();

    public WPStyleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setAntiAlias(true);

        titleTextPaint = new Paint();
        titleTextPaint.setAntiAlias(true);
        titleTextPaint.setTextSize(30);
        titleTextPaint.setTextAlign(Paint.Align.CENTER);
        titleTextPaint.setColor(Color.WHITE);

        contentTextPaint = new Paint();
        contentTextPaint.setAntiAlias(true);
        contentTextPaint.setTextSize(20);
        contentTextPaint.setTextAlign(Paint.Align.CENTER);
        contentTextPaint.setColor(Color.WHITE);

        if (icoRes.length > 0 && icoRes != null)
            initBitmap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        smallRectWidth = measuredWidth / 4 - interval + interval / 2;
        smallRectHeight = measuredHeight / 4 - interval;
        bigRectWidth = measuredWidth / 2 - interval;
        bigRectHeight = measuredHeight / 2 - interval;
    }


    int j = 0;
    int beginHeight = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        j = 0;
        beginHeight = 0;
        for (int i = 0; i <= 1; i++) {
            drawBigRect(canvas, i);
            drawTitle(canvas, bigRectWidth, bigRectHeight, i, titleTextPaint);
            drawIcon(canvas, bigRectWidth, bigRectHeight, i, contentTextPaint);
            drawContent(canvas, bigRectWidth, bigRectHeight, i, contentTextPaint);
        }
        beginHeight += bigRectHeight;

        drawRect(canvas, measuredWidth - interval * 2, smallRectHeight, 0);
        drawTitle(canvas, smallRectWidth, smallRectHeight, 1, titleTextPaint);
        canvas.drawBitmap(icoBitmap.get(j), smallRectWidth / 2,
                bigRectHeight / 4 + beginHeight, contentTextPaint);
        drawContent(canvas, smallRectWidth, smallRectHeight, 1, contentTextPaint);

        beginHeight += smallRectHeight;

        for (int i = 0; i < 2; i++) {
            drawSmallRect(canvas, i);
            drawTitle(canvas, smallRectWidth, smallRectHeight, i, contentTextPaint);
            drawIcon(canvas, smallRectWidth, smallRectHeight, i, contentTextPaint);
            drawContent(canvas, smallRectWidth, smallRectHeight, i, contentTextPaint);
        }
        drawRect(canvas, bigRectWidth, smallRectHeight, 1);
        drawTitle(canvas, bigRectWidth, smallRectHeight, 1, titleTextPaint);
        drawIcon(canvas, bigRectWidth, smallRectHeight, 1, contentTextPaint);
        drawContent(canvas, bigRectWidth, smallRectHeight, 1, contentTextPaint);
    }

    private void drawRect(Canvas canvas, int width, int height, int i) {
        rectPaint.setColor(Color.parseColor(color[j]));
        canvas.drawRect(interval + width * i,
                interval + beginHeight, width * (i + 1), height + beginHeight, rectPaint);
    }

    private void drawBigRect(Canvas canvas, int i) {
        drawRect(canvas, bigRectWidth, bigRectHeight, i);
    }

    private void drawSmallRect(Canvas canvas, int i) {
        drawRect(canvas, smallRectWidth, smallRectHeight, i);
    }

    private void drawTitle(Canvas canvas, int width, int height, int i, Paint paint) {
        canvas.drawText(text[j], (float) ((i + 0.5) * width), (float) (height / 1.4 + beginHeight), paint);
    }

    private void drawContent(Canvas canvas, int width, int height, int i, Paint paint) {
        canvas.drawText(content[j++], (float) ((i + 0.5) * width), (float) (height / 1.1 + beginHeight), paint);
    }

    private void drawIcon(Canvas canvas, int width, int height, int i, Paint paint) {
        canvas.drawBitmap(icoBitmap.get(j), (float) ((2 * i + 1) * width + interval - icoBitmap.get(0).getWidth()) / 2 + 5,
                height / 6 + beginHeight, paint);
    }


    public void initAllRes(String[] text, String[] content, String[] color, int icoRes[]) {
        this.text = text;
        this.content = content;
        this.color = color;
        this.icoRes = icoRes;

    }

    public void initTextContent(String[] content) {
        this.content = content;
    }

    private void initBitmap() {
        for (int i = 0; i < icoRes.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icoRes[i]);
            icoBitmap.add(bitmap);
        }
    }
}
