package com.example.apiexcute2.xposed;

import android.util.Log;
import android.view.View;


import com.example.apiexcute2.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;

public class ViewOnDraw extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        View view = (View) param.thisObject;
        String viewPath = ViewUtil.getViewPath(view);
        Log.i("LZH","viewPath: "+viewPath);
    }
}

