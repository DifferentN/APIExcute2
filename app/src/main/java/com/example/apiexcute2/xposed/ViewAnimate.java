package com.example.apiexcute2.xposed;

import android.view.View;
import android.view.ViewPropertyAnimator;


import com.example.apiexcute2.dataRecord.ViewAnimationScrollRecord;
import com.example.apiexcute2.listener.MyAnimatorListener;

import de.robv.android.xposed.XC_MethodHook;

public class ViewAnimate extends XC_MethodHook {
    private ViewAnimationScrollRecord record = ViewAnimationScrollRecord.getInstance();
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        View view = (View) param.thisObject;
        ViewPropertyAnimator animator = (ViewPropertyAnimator) param.getResult();
        record.addViewPropertyAnimatorRecord(animator,view);
        animator.setListener(new MyAnimatorListener(animator,null));
    }
}
