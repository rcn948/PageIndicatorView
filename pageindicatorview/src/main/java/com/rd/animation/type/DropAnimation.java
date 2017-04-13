package com.rd.animation.type;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.rd.animation.controller.ValueController;
import com.rd.animation.data.type.DropAnimationValue;

public class DropAnimation extends BaseAnimation<AnimatorSet> {

    private DropAnimationValue value;
    private enum AnimationType {Width, Height, Radius}

    private int widthStart;
    private int widthEnd;
    private int center;
    private int radius;

    public DropAnimation(@NonNull ValueController.UpdateListener listener) {
        super(listener);
        value = new DropAnimationValue();
    }

    @NonNull
    @Override
    public AnimatorSet createAnimator() {
        AnimatorSet animator = new AnimatorSet();
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        return animator;
    }

    @Override
    public DropAnimation progress(float progress) {
        if (animator != null) {
            long playTimeLeft = (long) (progress * animationDuration);
            boolean isReverse = false;

            for (Animator anim : animator.getChildAnimations()) {
                ValueAnimator animator = (ValueAnimator) anim;
                long animDuration = animator.getDuration();
                long currPlayTime = playTimeLeft;

                if (isReverse) {
                    currPlayTime -= animDuration;
                }

                if (currPlayTime < 0) {
                    continue;

                } else if (currPlayTime >= animDuration) {
                    currPlayTime = animDuration;
                }

                if (animator.getValues() != null && animator.getValues().length > 0) {
                    animator.setCurrentPlayTime(currPlayTime);
                }

                if (!isReverse && animDuration >= animationDuration) {
                    isReverse = true;
                }
            }
        }

        return this;
    }

    @Override
    public DropAnimation duration(long duration) {
        super.duration(duration);
        return this;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public DropAnimation with(int widthStart, int widthEnd, int center, int radius) {
        if (hasChanges(widthStart, widthEnd, center, radius)) {
            animator = createAnimator();

            this.widthStart = widthStart;
            this.widthEnd = widthEnd;

            this.center = center;
            this.radius = radius;

            int heightFromValue = center;
            int heightToValue = center / 3;

            int fromRadius = radius;
            int toRadius = (int) (radius / 1.5);
            long halfDuration = animationDuration / 2;

            ValueAnimator widthAnimator = createValueAnimation(widthStart, widthEnd, animationDuration, AnimationType.Width);
            ValueAnimator heightForwardAnimator = createValueAnimation(heightFromValue, heightToValue, halfDuration, AnimationType.Height);
            ValueAnimator heightBackwardAnimator = createValueAnimation(heightToValue, heightFromValue, halfDuration, AnimationType.Height);

            ValueAnimator radiusForwardAnimator = createValueAnimation(fromRadius, toRadius, halfDuration, AnimationType.Radius);
            ValueAnimator radiusBackwardAnimator = createValueAnimation(toRadius, fromRadius, halfDuration, AnimationType.Radius);

            animator.play(heightForwardAnimator).with(radiusForwardAnimator).with(widthAnimator).before(heightBackwardAnimator).before(radiusBackwardAnimator);
        }

        return this;
    }

    private ValueAnimator createValueAnimation(int fromValue, int toValue, long duration, final AnimationType type) {
        ValueAnimator anim = ValueAnimator.ofInt(fromValue, toValue);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                onAnimatorUpdate(animation, type);
            }
        });

        return anim;
    }

    private void onAnimatorUpdate(@NonNull ValueAnimator animation, @NonNull AnimationType type) {
        int frameValue = (int) animation.getAnimatedValue();

        switch (type) {
            case Width:
                value.setWidth(frameValue);
                break;

            case Height:
                value.setWidth(frameValue);
                break;

            case Radius:
                value.setRadius(frameValue);
                break;
        }

        if (listener != null) {
            listener.onDropAnimationUpdated(value);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean hasChanges(int widthStart, int widthEnd, int center, int radius) {
        if (this.widthStart != widthStart) {
            return true;
        }

        if (this.widthEnd != widthEnd) {
            return true;
        }

        if (this.center != center) {
            return true;
        }

        if (this.radius != radius) {
            return true;
        }

        return false;
    }

}
