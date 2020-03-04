package com.example.apiexcute2.xposed;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;

public class HookMethod extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Object obj = param.thisObject;
        String flag = "";
        if(obj instanceof ObjectAnimator){
            flag = ((ObjectAnimator)obj).toString();
        }else{
            flag = ((ValueAnimator)obj).toString();
        }
        Log.i("LZH","end animation: "+flag);
    }
}
