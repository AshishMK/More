package com.example.more.utills.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;

import com.example.more.R;
import com.example.more.utills.Screen;
import com.example.more.utills.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Perform circular animation on 2015/10/19.
 */
public class BallScaleMultipleIndicator extends BaseIndicatorController {

    float[] scaleFloats = new float[]{1, 1, 1};
    int[] alphaInts = new int[]{255, 255, 255};


    @Override
    public void draw(Canvas canvas, Paint paint) {
        float circleSpacing = Screen.dp(4); // paint.setColor(Color.parseColor("#00828F"));

        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                paint.setAlpha(140);//  paint.setColor(Color.parseColor("#400097A8"));
            } else if (i == 2) {
                //   paint.setColor(Color.parseColor("#00828F"));
            } else
                paint.setAlpha(90);// paint.setColor(Color.parseColor("#200097A8"));
//            if(i==2)
            canvas.scale(scaleFloats[i], scaleFloats[i], getWidth() / 2, getHeight() / 2);
            if (i == 2)
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - (circleSpacing + Screen.dp(4)), paint);
            else if (i == 1)
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - (circleSpacing + Screen.dp(2)), paint);
            else if (i == 0)
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);


        }
            canvas.drawBitmap(Utils.getBitmapFromVectorDrawable(getTarget().getContext(),R.drawable.ic_thin_arrowheads_pointing_down),(getWidth()/2)-Screen.dp(9),(getHeight()/2)-Screen.dp(9
            ),null);
    }

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList<>();
        long[] delays = new long[]{0, 000, 000};
        for (int i = 0; i < 3; i++) {

            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(.90f, 1);
            scaleAnim.setInterpolator(new LinearInterpolator());
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setRepeatMode(ValueAnimator.REVERSE);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //      if(index!=2)
                    scaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.setStartDelay(delays[i]);

            scaleAnim.start();

            ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 255);
            alphaAnim.setInterpolator(new LinearInterpolator());
            alphaAnim.setDuration(1000);
            alphaAnim.setRepeatMode(ValueAnimator.REVERSE);
            alphaAnim.setRepeatCount(-1);
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //   if(index!=2)
                    //     alphaInts[index] = (int) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.setStartDelay(delays[i]);
            alphaAnim.start();

            animators.add(scaleAnim);
            animators.add(alphaAnim);
        }
        return animators;
    }

}
