package com.example.apiexcute2.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class LogViewHierarchyUtil {
    private static final String VIEW_NAME = "viewName";
    private static final String CHILDS = "childs";
    private static final String CHILD_SIZE = "childSize";
    private static final String VIEW_INDEX = "viewIndex";
    public static void logActivityView(String logFileName, Activity activity){
        View decorView = activity.getWindow().getDecorView();
        JSONObject viewInfo = getViewJSON(decorView);
        FileWriterUtil.writeJson(logFileName,viewInfo);
        Log.i("LZH","GET JSON");
    }
    public static void logWindowView(String logFileName, Context context){
        Object windowManagerImpl = context.getSystemService(Context.WINDOW_SERVICE);
        Class windowManagerImplClass = windowManagerImpl.getClass();
        Class windowManagerGlobalClass = null;
        Object windowManager = null;
        ArrayList<View> mViews = new ArrayList<>();
        try {
            Field field1 = windowManagerImplClass.getDeclaredField("mGlobal");
            field1.setAccessible(true);
            windowManager = field1.get(windowManagerImpl);
            windowManagerGlobalClass = windowManager.getClass();
            Field field2 = windowManagerGlobalClass.getDeclaredField("mViews");
            field2.setAccessible(true);
            mViews = (ArrayList<View>) field2.get(windowManager);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        JSONArray viewJSONArray = new JSONArray();
        int index = 0;
        Log.i("LZH","view size: "+mViews.size());
        for(View view:mViews){
            Log.i("LZH","viewName: "+view.getClass().getName()+"visibility: "+(view.getVisibility()==View.VISIBLE));
//            if(view instanceof LinearLayout){
//                ViewUtil.showView(view);
//            }
            JSONObject viewInfo = getViewJSON(view);
            viewInfo.put(VIEW_INDEX,index+"");
            index++;
            viewJSONArray.add(viewInfo);
        }
        FileWriterUtil.writeJson(logFileName,viewJSONArray);
    }
    public static JSONObject getViewJSON(View view){
        ViewGroup viewGroup = null;
        if(view instanceof ViewGroup){
            viewGroup = (ViewGroup) view;
        }
        JSONObject viewInfo = new JSONObject();
        viewInfo.put(VIEW_NAME,view.getClass().getName());
        JSONArray childInfos = new JSONArray();
        viewInfo.put(CHILDS,childInfos);
        viewInfo.put(CHILD_SIZE,0);
        if(viewGroup==null){
            return viewInfo;
        }
        viewInfo.put(CHILD_SIZE,viewGroup.getChildCount());
        for(int i=0;i<viewGroup.getChildCount();i++){
            childInfos.add(getViewJSON(viewGroup.getChildAt(i)));
        }
        return viewInfo;
    }
}
