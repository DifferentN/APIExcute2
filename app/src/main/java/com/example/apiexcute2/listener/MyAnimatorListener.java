package com.example.apiexcute2.listener;

import android.animation.Animator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.example.apiexcute2.Event.AnimatorNode;
import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.dataRecord.ViewAnimationScrollRecord;
import com.example.apiexcute2.util.ProcessEventUtil;
import com.example.apiexcute2.util.ViewUtil;


public class MyAnimatorListener implements Animator.AnimatorListener {
    private Animator.AnimatorListener originalListener;
    private int currentapiVersion=android.os.Build.VERSION.SDK_INT;
    private ViewAnimationScrollRecord record;
    private ViewPropertyAnimator viewPropertyAnimator;
    public MyAnimatorListener(ViewPropertyAnimator animator,Animator.AnimatorListener listener){
        originalListener = listener;
        viewPropertyAnimator = animator;
        record = ViewAnimationScrollRecord.getInstance();
    }
    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {
        if(originalListener==null){
            return;
        }
        if(currentapiVersion>=26){
            originalListener.onAnimationStart(animation,isReverse);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {
        if(originalListener==null){
            return;
        }
        if(currentapiVersion>=26){
            originalListener.onAnimationEnd(animation,isReverse);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if(originalListener==null){
            return;
        }
        originalListener.onAnimationStart(animation);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(originalListener==null){
            return;
        }
        View view = record.getViewByViewPropertyAnimator(viewPropertyAnimator);
        String viewPath = ViewUtil.getViewPath(view);
//        MyEvent myEvent = MethodTrackPool.getInstance().getMyEvent();
//        ProcessEventUtil.updateEventState(viewPath, AnimatorNode.ViewPropertyAnimatorType,myEvent);
//        record.removeViewPropertyAnimatorRecord(viewPropertyAnimator);
//        originalListener.onAnimationEnd(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if(originalListener==null){
            return;
        }
        originalListener.onAnimationCancel(animation);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        if(originalListener==null){
            return;
        }
        originalListener.onAnimationRepeat(animation);
    }
}
