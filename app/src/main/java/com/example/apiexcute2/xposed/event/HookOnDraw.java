package com.example.apiexcute2.xposed.event;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;


import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.Event.MyEvent;
import com.example.apiexcute2.receive.LocalActivityReceiver;
import com.example.apiexcute2.util.ActivityUtil;
import com.example.apiexcute2.util.ProcessEventUtil;

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
        Activity activity = getActivity(view);
        MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
        MyEvent myEvent = methodTrackPool.getMyEvent();
        if(myEvent==null){
            return;
        }
        String viewPath = myEvent.getPath();
        if(!viewPath.equals(myEvent.getPath())){
            return;
        }
        if(ProcessEventUtil.checkEventState(myEvent)&&
                activity.getClass().getName().equals(ActivityUtil.getCurActivityName())){
            Intent intent = new Intent();
            intent.setAction(LocalActivityReceiver.EXECUTE_EVENT);
            activity.sendBroadcast(intent);
        }
    }
    private Activity getActivity(View view){
        if(view!=null){
            Context context = view.getContext();
            while(context instanceof ContextWrapper){
                if(context instanceof Activity){
                    return (Activity)context;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
        }
        return null;
    }
}
