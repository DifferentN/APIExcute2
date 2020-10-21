package com.example.apiexcute2.xposed.event;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;

public class HookFragmentOnResume extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log.i("LZH","fragment onResume");
    }
}
