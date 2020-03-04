package com.example.apiexcute2.xposed;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;


import com.example.apiexcute2.listener.MyAnimatorUpdateListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

public class ValueAnimatorAddUpdateListener extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        ValueAnimator valueAnimator = (ValueAnimator) param.thisObject;
        if(valueAnimator instanceof ObjectAnimator){
            return;
        }
        Class valueAnimatorClazz = ValueAnimator.class;
        Field field = valueAnimatorClazz.getDeclaredField("mUpdateListeners");
        field.setAccessible(true);
        ArrayList<ValueAnimator.AnimatorUpdateListener> mUpdateListeners = (ArrayList<ValueAnimator.AnimatorUpdateListener>) field.get(valueAnimator);
        for(int i=mUpdateListeners.size()-1;i>=0;i--){
            ValueAnimator.AnimatorUpdateListener listener = mUpdateListeners.get(i);
            if(!(listener instanceof MyAnimatorUpdateListener)){
                MyAnimatorUpdateListener myListener = new MyAnimatorUpdateListener(valueAnimator,listener);
                mUpdateListeners.remove(i);
                mUpdateListeners.add(i,myListener);
            }
        }
    }
}
