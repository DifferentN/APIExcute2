package com.example.apiexcute2.Event;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MethodTrackPool {
    private Context context;
    private static volatile MethodTrackPool methodTrackPool;
    private static List<MyEvent> events;
    private static MyEvent curEvent;
    private boolean executeActionState = false; //false 表示当前操作未执行
    private static boolean isAvailable = false;
    private static int myEventPoint = 0;
    public MethodTrackPool(){
        readSequence("ankiLogDetail.txt");
    }
    public static MethodTrackPool getInstance(){
        if(methodTrackPool==null){
            synchronized (MethodTrackPool.class){
                if(methodTrackPool==null){
                    methodTrackPool = new MethodTrackPool();
                }
            }
        }
        return methodTrackPool;
    }
    private void readSequence(String fileName){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        File file = new File(filePath);
        StringBuffer buf = new StringBuffer();
        try {
            if(!file.exists()){
                Log.i("LZH","序列文件不存在");
            }
            FileReader fileReader  = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while( (line = reader.readLine())!=null ){
                buf.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = JSONArray.parseArray(buf.toString());
        events = new ArrayList<>();
        MyEvent myEvent = null;
        for(int i=0;i<jsonArray.size();i++){
            myEvent = transformJSONToMyEvent(jsonArray.getJSONObject(i));
            events.add(myEvent);
        }
    }
    private MyEvent transformJSONToMyEvent(JSONObject jsonObject){
        String activityId = jsonObject.getString(MyEvent.ACTIVITY_ID);
        String viewID = jsonObject.getString(MyEvent.VIEW_ID);
        String viewPath = jsonObject.getString(MyEvent.VIEW_PATH);
        String methodName = jsonObject.getString(MyEvent.METHOD_NAME);
        String parameter = jsonObject.getString(MyEvent.PARAMETER_VALUE);
        MyEvent myEvent = new MyEvent(activityId,viewID,viewPath,methodName);
        myEvent.setParameters(parameter);
        JSONArray animatorViews = jsonObject.getJSONArray(MyEvent.SNAPSHOT);
        List<AnimatorNode> snapshot = new ArrayList<>();
        for(int i=0;i<animatorViews.size();i++){
            snapshot.add(transformJSONToAnimatorNode(animatorViews.getJSONObject(i)));
        }
        myEvent.setSnapshot(snapshot);
        return myEvent;
    }
    private AnimatorNode transformJSONToAnimatorNode(JSONObject jsonObject){
        String viewPath = jsonObject.getString(MyEvent.VIEW_PATH);
        AnimatorNode animatorNode = new AnimatorNode(viewPath);
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ObjectAnimatorType,
                jsonObject.getIntValue(AnimatorNode.ObjectAnimatorType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ValueAnimatorType,
                jsonObject.getIntValue(AnimatorNode.ValueAnimatorType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.AnimationType,
                jsonObject.getIntValue(AnimatorNode.AnimationType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ScrollerType,
                jsonObject.getIntValue(AnimatorNode.ScrollerType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.OverScrollerType,
                jsonObject.getIntValue(AnimatorNode.OverScrollerType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ViewPropertyAnimatorType,
                jsonObject.getIntValue(AnimatorNode.ViewPropertyAnimatorType));
        return animatorNode;
    }
    public MyEvent getMyEvent(){
        if(myEventPoint>=events.size()){
            return null;
        }
        return events.get(myEventPoint);
    }
    public void finishCurEvent(){
        myEventPoint++;
    }
    public static boolean isAvailable(){
        return isAvailable;
    }
    public void setAvailable(boolean b){
        isAvailable = b;
    }
    private void sendNotification(MyEvent myEvent){

    }

    public void setContext(Context context){
        this.context = context;
    }

    public void LaunchUserAction(){
        if(!isAvailable()){
            return;
        }

    }

    private boolean checkEqual(String curMethod,String invoke){
        int start = 0,end = 0;
        while(start>=0){
            start = curMethod.indexOf("(",start);
            end = curMethod.indexOf(":",start);
            if(end<0){
                end = curMethod.indexOf(")",start);
            }
            if(start<0){
                break;
            }
            if(!invoke.contains(curMethod.substring(start,end))){
                return false;
            }
            start = end+1;
        }
        return true;
    }
}
