package com.example.apiexcute2.xposed;

import android.view.View;


import com.example.apiexcute2.dataRecord.AnimViewRelationRecog;

import de.robv.android.xposed.XC_MethodHook;

public class ViewComputeScroll extends XC_MethodHook {
    private AnimViewRelationRecog relationRecog = AnimViewRelationRecog.getInstance();
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        View view = (View) param.thisObject;
//        if(view.getClass().getName().contains("DrawerLayout")){
//            Log.i("LZH","before view computeScroll ");
//        }
        relationRecog.setViewScroll(view);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        View view = (View) param.thisObject;
//        if(view.getClass().getName().contains("DrawerLayout")){
//            Log.i("LZH","after view computeScroll ");
//        }
        relationRecog.clearViewScroll();
    }
}
