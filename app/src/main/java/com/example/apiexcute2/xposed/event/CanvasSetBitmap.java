package com.example.apiexcute2.xposed.event;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.example.apiexcute2.util.ProcessEventUtil;

import de.robv.android.xposed.XC_MethodHook;

public class CanvasSetBitmap extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        View view = (View) param.thisObject;
        int x = (int) param.args[0];

//        Log.i("LZH","view Name: "+view.getClass().getName()+"x: "+param.args[0]+" y:"+param.args[1]);
    }
}
