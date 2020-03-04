package com.example.apiexcute2.dataRecord;

import android.animation.ValueAnimator;
import android.view.View;

public class AnimViewRelationRecog {
    private ValueAnimator valueAnimator;
    private View viewScroll;
    private static AnimViewRelationRecog animViewRelationRecog;
    private ViewAnimationScrollRecord animScrollRecord;
    public AnimViewRelationRecog(){
        animScrollRecord = ViewAnimationScrollRecord.getInstance();
    }
    public static AnimViewRelationRecog getInstance(){
        if(animViewRelationRecog==null){
            synchronized (AnimViewRelationRecog.class){
                if(animViewRelationRecog==null){
                    animViewRelationRecog = new AnimViewRelationRecog();
                }
            }
        }
        return animViewRelationRecog;
    }

    /**
     * 在AnimatorUpdateListener.onAnimationUpdate之前,调用此方法，
     * 用来保存animator
     * @param valueAnimator
     */
    public void setValueAnimator(ValueAnimator valueAnimator){
        this.valueAnimator = valueAnimator;
    }

    /**
     * 在AnimatorUpdateListener.onAnimationUpdate之后,调用此方法，
     * 用来清除animator
     */
    public void clearValueAnimator(){
        valueAnimator = null;
    }

    /**
     * view.invalidate或postInvalidate调用时 调用此方法，给view和animator设置关联
     * @param view
     */
    public void recordAnimatorView(View view){
        if(valueAnimator!=null){
            if(!animScrollRecord.containValueAnimatorView(valueAnimator,view)){
                animScrollRecord.addValueAnimatorRecord(valueAnimator,view);
            }
        }
        valueAnimator = null;
    }

    /**
     * 在调用view.computeScroll之前，调用此方法，注意调用对象可能重写了此方法，且没有调用super.xxx
     * @param view
     */
    public void setViewScroll(View view){
        viewScroll = view;
    }

    /**
     * 在调用view.computeScroll之后，调用此方法
     */
    public void clearViewScroll(){
        viewScroll = null;
    }

    /**
     * scroll/OverScroll.computeScrollOffset调用时，调用此方法
     * @param obj
     */
    public void recordScrollView(Object obj){
        if(viewScroll!=null){
            if(!animScrollRecord.containScrollView(obj,viewScroll)){
                animScrollRecord.addScrollRecord(obj,viewScroll);
            }
        }
    }
}
