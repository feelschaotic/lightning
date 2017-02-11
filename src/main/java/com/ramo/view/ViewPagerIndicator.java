package com.ramo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ramo on 2016/3/10.
 */
public class ViewPagerIndicator extends LinearLayout {

    private int offsetX;
    private int initX = 0;
    private int visibleTagCount = 4;
    private int triangleWidth;
    private int triangleHeight;
    private int tabWidth;

    private Canvas canvas;
    private Paint paint;
    private Path path;

    private ViewPager view_pager;
    /**
     * 三角形最大底边宽度
     */
    private final int TRIANGLE_MAX_WIDTH = (int) (getScreenWidth() / 3 * 0.14);


    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(new CornerPathEffect(3));//设置闭合的角模糊度 不要太尖锐
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(offsetX, getHeight());
        canvas.drawPath(path, paint);

        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //确定绘制的三角形的宽高
        determineTriangleWidthAndHeight(w, h);
        determineOffsetX(w, h);//确定偏移量
        tabWidth = getWidth() / visibleTagCount;
    }

    private void determineOffsetX(int w, int h) {
        offsetX = w / visibleTagCount / 2 - triangleWidth / 2;
        initX = offsetX;
    }

    private void determineTriangleWidthAndHeight(int w, int h) {
        triangleWidth = (int) (w / visibleTagCount * 0.15);
        triangleWidth = Math.min(triangleWidth, TRIANGLE_MAX_WIDTH);
        triangleHeight = triangleWidth / 2;

        drawTriangle();
    }

    private void drawTriangle() {
        path = new Path();
        path.moveTo(0, 0);
        path.lineTo(triangleWidth, 0);
        path.lineTo(triangleWidth / 2, -triangleHeight - 2);
        path.close();
    }

    /*
    移动指示器
     */
    public void scroll(int position, float positionOffset) {
        offsetX = (int) (tabWidth * (position + positionOffset)) + initX;
        lightTab(getChildAt(position));

        if (position >= visibleTagCount - 2 && positionOffset > 0 && getChildCount() > visibleTagCount) {
            if (visibleTagCount != 1) {
                scrollView(position, positionOffset);
            } else {
                this.scrollTo(position * getWidth() + (int) (getWidth() * positionOffset), 0);
            }
        }
        invalidate();
    }

    /*
    移动整个控件
     */
    private void scrollView(int position, float positionOffset) {

        this.scrollTo((int) ((position - visibleTagCount + 2) * tabWidth +
                tabWidth * positionOffset), 0);
    }

    /**
     * 通过布局文件设置布局
     */
    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount == 0)
            return;
        LinearLayout.LayoutParams layoutParams;
        View childAt;
        for (int i = 0; i < childCount; i++) {
            childAt = getChildAt(i);
            layoutParams = (LayoutParams) childAt.getLayoutParams();
            layoutParams.width = getScreenWidth() / visibleTagCount;
            layoutParams.weight = 0;
            childAt.setLayoutParams(layoutParams);
        }
        setOnClickListener();
        super.onFinishInflate();
    }

    public void setVisibleTagCount(int count) {
        if (count > 0)
            visibleTagCount = count;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getScreenWidth() {

        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 动态生成tab
     *
     * @param tabTitles
     */
    LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    public void setTabItemTitles(List<String> tabTitles) {
        if (tabTitles != null && tabTitles.size() > 0) {
            this.removeAllViews();

            layoutParams.width = getScreenWidth() / visibleTagCount;
            layoutParams.weight = 0;
            for (String title : tabTitles) {
                addView(title);
            }
            setOnClickListener();
        }
    }

    private final int TAB_TEXT_NORMAL_COLOR = 0x77FFFFFF;
    private final int TAB_TEXT_SELECTED_COLOR = 0xFFFFFFFF;

    private void addView(String title) {

        TextView view = new TextView(getContext());
        view.setText(title);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(17);
        view.setTextColor(TAB_TEXT_NORMAL_COLOR);

        view.setLayoutParams(layoutParams);
        this.addView(view);
    }

    public void setOnClickListener() {
        int childCount = getChildCount();
        if (childCount == 0)
            return;
        for (int i = 0; i < childCount; i++) {
            final int pos = i;
            final TextView childAt = (TextView) getChildAt(i);
            childAt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    view_pager.setCurrentItem(pos);
                }
            });
        }
    }

    private void lightTab(View v) {
        restoreAllTabColor();
        if (v instanceof TextView)
            ((TextView) v).setTextColor(TAB_TEXT_SELECTED_COLOR);
    }

    private void restoreAllTabColor() {
        int childCount = getChildCount();
        if (childCount == 0)
            return;
        TextView childAt;
        for (int i = 0; i < childCount; i++) {
            childAt = (TextView) getChildAt(i);
            childAt.setTextColor(TAB_TEXT_NORMAL_COLOR);
        }
    }

    public void setView_pager(ViewPager view_pager) {
        this.view_pager = view_pager;
    }
}