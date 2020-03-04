package com.example.apiexcute2.xposed;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;

public class ViewStartAniamtion extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        boolean res = (boolean) param.getResult();
        if(!res){
            Log.i("LZH","computeScrollOffset: "+res);
        }
    }
}
