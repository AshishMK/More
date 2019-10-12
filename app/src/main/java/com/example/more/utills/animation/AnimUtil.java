package com.example.more.utills.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.utills.Screen;

import timber.log.Timber;

/**
 * Class provide various animation for the views in application wide
 */
public class AnimUtil {

    /**
     * Rotate the target view with animation
     *
     * @param view
     */
    public static void rotateView(View view) {
        int height = view.getHeight();
        int width = view.getHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, 0.0f, 360.0f);
        ObjectAnimator pivotX = ObjectAnimator.ofFloat(view, "pivotX", width / 2);
        ObjectAnimator pivotY = ObjectAnimator.ofFloat(view, "pivotY", height / 2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(3500);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(objectAnimator, pivotX, pivotY);
        view.setTag(animatorSet);
        animatorSet.start();
    }

    /**
     * Rotate the target view with animation
     *
     * @param view
     */
    public static void translateUpDown(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0.0f, Screen.dp(8),0,Screen.dp(8));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(2400);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(objectAnimator);
        view.setTag(animatorSet);
        animatorSet.start();
    }

    /**
     * method to apply reveal animation on target view
     *
     * @param viewRoot
     */
    public static void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(AppController.getInstance().getResources().getInteger(R.integer.anim_duration_mid_long));
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }


}
