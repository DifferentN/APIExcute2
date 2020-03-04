package com.example.apiexcute2.xposed;

import android.util.Log;


import com.example.apiexcute2.dataRecord.AnimViewRelationRecog;

import de.robv.android.xposed.XC_MethodHook;

public class ScrollComputeScrollOffset extends XC_MethodHook {
    private AnimViewRelationRecog relationRecog = AnimViewRelationRecog.getInstance();
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Object obj = param.thisObject;
        boolean res = (boolean) param.getResult();
        if(!res){
            Log.i("LZH","scroll finish");
        }
        relationRecog.recordScrollView(obj);
    }
}
