package com.example.apiexcute2.listener;

import android.animation.ValueAnimator;

import com.example.apiexcute2.dataRecord.AnimViewRelationRecog;


public class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator.AnimatorUpdateListener originalListener;
    private ValueAnimator valueAnimator;
    private AnimViewRelationRecog relationRecog;
    public MyAnimatorUpdateListener(ValueAnimator animator, ValueAnimator.AnimatorUpdateListener listener){
        valueAnimator = animator;
        originalListener = listener;
        relationRecog = AnimViewRelationRecog.getInstance();
    }
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        relationRecog.setValueAnimator(valueAnimator);
        originalListener.onAnimationUpdate(animation);
        relationRecog.clearValueAnimator();
    }
}
