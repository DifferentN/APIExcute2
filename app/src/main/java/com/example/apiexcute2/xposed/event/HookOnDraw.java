package com.example.apiexcute2.xposed.event;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;


import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.eventModel.ViewInfo;
import com.example.apiexcute2.model.workModel.WorkItem;
import com.example.apiexcute2.monitor.OnDrawMonitor;
import com.example.apiexcute2.receive.LocalActivityReceiver;
import com.example.apiexcute2.util.ActivityUtil;
import com.example.apiexcute2.util.MatchUtil;
import com.example.apiexcute2.util.ProcessEventUtil;
import com.example.apiexcute2.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class HookOnDraw extends XC_MethodHook {
    private String fileName = "methodLog.txt";
    public HookOnDraw(){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        View view = (View) param.thisObject;
//        Log.i("LZH","draw(CVl): "+view.getClass().getName());
        OnDrawMonitor onDrawMonitor = OnDrawMonitor.getInstance();
        //发送一个onDraw通知
        onDrawMonitor.sendOnDraw(view);

//        if(view.getClass().getName().contains("DecorView")){
//            OnDrawMonitor onDrawMonitor = OnDrawMonitor.getInstance();
//            //发送一个onDraw通知
//            onDrawMonitor.sendOnDraw(view);
//
////            Log.i("LZH","DecorView draw");
//        }

    }
}
