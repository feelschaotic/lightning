package com.ramo.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AnimUtil {
    private static int i = 1;

    /*
     * 左淡出动画
     */
    @SuppressLint("NewApi")
    public static void leftOut(View view, int j) {
        if (1 == j)
            i = 1;

        ValueAnimator animator1 = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        animator1.setDuration(1000);
        animator1.setInterpolator(new AccelerateInterpolator());

        ValueAnimator animator2 = ObjectAnimator.ofFloat(view, "x", view.getX(), (view.getX() - view.getWidth() * 2));// 向左移动效果
        animator2.setDuration(800);
        animator2.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(i * 300);
        animatorSet.playTogether(animator1, animator2);
        animatorSet.start();
        i++;
    }

    public static void zoom(View view) {


    }


}
