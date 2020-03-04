package com.example.apiexcute2.util;

import android.app.Activity;
import android.content.Context;

public class ActivityUtil {
    private static String curActivityName;
    public static String getCurActivityName(){
        return curActivityName;
    }
    public static  void setCurActivityName(String activityName){
        curActivityName = activityName;
    }
}
