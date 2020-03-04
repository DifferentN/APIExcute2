package com.example.apiexcute2.xposed;

import android.animation.Animator;
import android.view.ViewPropertyAnimator;


import com.example.apiexcute2.listener.MyAnimatorListener;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;

public class ViewPropertySetAnimatorListener extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        ViewPropertyAnimator viewPropertyAnimator = (ViewPropertyAnimator) param.thisObject;
        Animator.AnimatorListener originalAnimatorListener = (Animator.AnimatorListener) param.args[0];
        MyAnimatorListener myListener = new MyAnimatorListener(viewPropertyAnimator,originalAnimatorListener);
        Class viewPropertyAnimatorClazz = ViewPropertyAnimator.class;
        Field field = viewPropertyAnimatorClazz.getDeclaredField("mListener");
        field.setAccessible(true);
        field.set(viewPropertyAnimator,myListener);
    }
}
