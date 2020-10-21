package com.example.apiexcute2.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ActivityUtil {
    private static String curActivityName;
    public static String getCurActivityName(){
        return curActivityName;
    }
    public static  void setCurActivityName(String activityName){
        curActivityName = activityName;
    }
    public static Activity getActivity(View view){
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
    public static String getTopActivityName(Context context){
        String topActivityName = null;
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
        if (taskInfos.size() > 0)
            topActivityName = taskInfos.get(0).topActivity.getClassName();
//        Log.i("LZH","topActivityPackageName: "+topActivityName);
        return topActivityName;
    }
}
