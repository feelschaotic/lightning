package com.ramo.view;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramo.file_transfer.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class TopBar extends RelativeLayout {

    private ImageView leftBtn, rightBtn = null;
    private TextView titleText = null;
    private LayoutParams leftParams, rightParams = null;

    private int leftTextColor;
    private Drawable leftBG;
    private String leftText;

    private int rightTextColor;
    private Drawable rightBG;
    private String rightText;

    private float titleTextSize;
    private int titleTextColor;
    private String title;
    private Drawable BG;

    private int leftAndRightBtnWidth = 40;
    private int leftAndRightBtnHeight = 35;

    private TopBarClickListener listener;

    public interface TopBarClickListener {
        void leftClick();

        void RightClick();
    }

    public void setOnTopBarClickListener(TopBarClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NewApi")
    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.topBar);

        leftTextColor = type.getColor(R.styleable.topBar_leftTextColor, 0);
        leftBG = type.getDrawable(R.styleable.topBar_leftBG);
        leftText = type.getString(R.styleable.topBar_leftText);

        rightTextColor = type.getColor(R.styleable.topBar_rightTextColor, 0);
        rightBG = type.getDrawable(R.styleable.topBar_rightBG);
        rightText = type.getString(R.styleable.topBar_rightText);

        titleTextColor = type.getColor(R.styleable.topBar_titleTextColor, 0);
        titleTextSize = type.getDimension(R.styleable.topBar_titleSize, 0);
        title = type.getString(R.styleable.topBar_topTitle);

        BG = type.getDrawable(R.styleable.topBar_BG);
        if (BG == null)
            BG = getResources().getDrawable(R.color.topbarBg);

        type.recycle();

        leftBtn = new ImageView(context);
        rightBtn = new ImageView(context);
        titleText = new TextView(context);

        leftBtn.setImageDrawable(leftBG);

        rightBtn.setImageDrawable(rightBG);

        titleText.setText(title);
        titleText.setTextSize(titleTextSize);
        titleText.setTextColor(titleTextColor);
        titleText.setGravity(Gravity.CENTER);

        setBackground(BG);

        // leftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // rightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        leftParams = new LayoutParams(leftAndRightBtnWidth, leftAndRightBtnHeight);
        rightParams = new LayoutParams(leftAndRightBtnWidth, leftAndRightBtnHeight);

        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        leftParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        rightParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);

        addView(leftBtn, leftParams);
        addView(rightBtn, rightParams);

        leftBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (listener != null)
                    listener.leftClick();
            }
        });

        rightBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (listener != null)
                    listener.RightClick();
            }
        });
    }

    public void setRightBg(Drawable rightBg) {
        rightBtn.setImageDrawable(rightBG);
    }

    public void setLeftBg(Drawable leftBG) {
        leftBtn.setImageDrawable(leftBG);
    }
    public ImageView getRightBtn() {
        return rightBtn;
    }
}
