package com.ramo.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.ramo.utils.RoundBitmapUtils;
import com.ramo.file_transfer.R;

public class RadarSearchDevicesView extends RelativeLayout {

    public static final String TAG = "SearchDevicesView";
    @SuppressWarnings("unused")
    private long TIME_DIFF = 1500;
    private float offsetArgs = 0;
    private boolean isSearching = true;

    private Bitmap radar_bg;
    private Bitmap center_head;
    private Bitmap args;

    public RadarSearchDevicesView(Context context) {
        super(context);
        initBitmap();
    }

    public RadarSearchDevicesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
    }

    public RadarSearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBitmap();
    }

    private void initBitmap() {
        if (radar_bg == null)
            radar_bg = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.radar_gplus_search_bg));
        if (center_head == null)
            center_head = RoundBitmapUtils.RtoRoundBitmap(getContext(), R.drawable.sidebar_head_superman, Color.WHITE);
        if (args == null)
            args = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.radar_gplus_search_args));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        canvas.drawBitmap(radar_bg, width / 2 - radar_bg.getWidth() / 2, height / 2 - radar_bg.getHeight() / 2, null);

        if (isSearching)
            moveArgs(canvas, width, height);

        drawCenterHead(canvas, width, height);

        if (isSearching) invalidate();
    }


    private void drawCenterHead(Canvas canvas, int width, int height) {
        if (offsetArgs != 0)
            canvas.rotate(-offsetArgs + 3, width / 2, height / 2);

        canvas.drawBitmap(center_head, width / 2 - center_head.getWidth() / 2, height / 2 - center_head.getHeight() / 2, null);
    }

    private void keepArgs(Canvas canvas, int width, int height) {
        canvas.drawBitmap(args, width / 2 - args.getWidth(), height / 2, null);
    }

    private void moveArgs(Canvas canvas, int width, int height) {
        Rect rMoon = new Rect(width / 2 - args.getWidth(), height / 2, width / 2, height / 2 + args.getHeight());
        canvas.rotate(offsetArgs, width / 2, height / 2);
        canvas.drawBitmap(args, null, rMoon, null);
        offsetArgs = offsetArgs + 3;
    }


    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(boolean isSearching) {
        this.isSearching = isSearching;
        offsetArgs = 0;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
             //   handleActionDownEvenet(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleActionDownEvenet(MotionEvent event) {
        RectF rectF = new RectF(getWidth() / 2 - center_head.getWidth() / 2,
                getHeight() / 2 - center_head.getHeight() / 2,
                getWidth() / 2 + center_head.getWidth() / 2,
                getHeight() / 2 + center_head.getHeight() / 2);

        if (rectF.contains(event.getX(), event.getY())) {
            Log.d(TAG, "click search device button");
            if (!isSearching()) {
                setSearching(true);
            } else {
                setSearching(false);
            }
        }
    }
}