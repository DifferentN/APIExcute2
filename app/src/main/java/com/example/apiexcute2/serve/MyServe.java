package com.example.apiexcute2.serve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.workModel.WorkItem;
import com.example.apiexcute2.util.FileWriterUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class MyServe extends NanoHTTPD {
    public static final String POST_DATA = "postData";
    public static final String METHOD_NAME = "methodName";
    public static final String SET_TEXT = "setText";
    public static final String PARAM = "parameterValue";
    private String pageContent;
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<ServeReceiver> serveReceiverWeakReference;
    public MyServe(int port) {
        super(port);
    }

    public MyServe(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        if(method==Method.POST){
            String url = session.getUri();
            String apiName = url.substring(1);
            HashMap<String,String> hashMap = new HashMap<>();
            try {
                session.parseBody(hashMap);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                Log.i("LZH",e.getMessage());
                e.printStackTrace();
            }
            //传入的参数按顺序，以;分割
            String paramValues = hashMap.get(POST_DATA);
            List<String> paramList = new ArrayList<>();
            if(paramValues!=null){
                String str[] = paramValues.split(";");
                for(String key:str){
                    paramList.add(key);
                }
            }
            startAPI(apiName,paramList);
        }
        byte[] bytes= pageContent.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK,"text/json",byteArrayInputStream,bytes.length);
        return  response;
    }

    private void startAPI(String apiName,List<String> paramList) {

        //根据APIName读取API模板
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+apiName+".txt";
        JSONObject workItemJson = FileWriterUtil.readJSONObject(path);
        Log.i("LZH","setInput");
        //给APIModel中的输入操作设置输入文本
        assignInput(paramList,workItemJson);

        //读取API模板中首页面的Activity类名
        JSONObject myEventJSON = workItemJson.getJSONObject(WorkItem.EVENT);
        String activityName = myEventJSON.getString(MyEvent.ACTIVITY_ID);
        // the coming launched app's packageName
        String packageName = myEventJSON.getString(MyEvent.PACKAGE_NAME);
        //指明API应该在哪个activity打开后执行
        serveReceiverWeakReference.get().setLaunchActivityName(activityName);
        //重新保存API模板
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ankiLogDetail.txt";
        FileWriterUtil.writeJson(savePath,workItemJson);
        //启动API
        serveReceiverWeakReference.get().executeAPI(apiName,packageName);
        Log.i("LZH","start API");
    }

    /**
     * 设置API中setText操作的输入参数
     * @param param
     * @param workItemJSON
     */
    private void assignInput(List<String> param,JSONObject workItemJSON){
        List<JSONObject> workItemList = new ArrayList<>();
        workItemList.add(workItemJSON);
        while(!workItemList.isEmpty()){
            JSONObject curWorkItemJson = workItemList.remove(0);
            JSONObject myEventJson = curWorkItemJson.getJSONObject(WorkItem.EVENT);
            //找到setText操作
            if(myEventJson.getString(MyEvent.METHOD_NAME).equals(SET_TEXT)){
                //根据参数类型，确定此setText操作要使用哪一个输入参数
                for(String item:param){
                    String tagValue[] = item.split(":");
                    if(tagValue[0].equals(myEventJson.getString(MyEvent.PARAMETER_TYPE))){
                        myEventJson.put(MyEvent.PARAMETER_VALUE,tagValue[1]);
                        break;
                    }
                }
            }
            JSONArray nextWorkItems = curWorkItemJson.getJSONArray(WorkItem.NEXT_WORK);
            for(int i=0;i<nextWorkItems.size();i++){
                workItemList.add(nextWorkItems.getJSONObject(i));
            }
        }
    }
    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public void setActivityWeakReference(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public void setServeReceiverWeakReference(ServeReceiver serveReceiver) {
        this.serveReceiverWeakReference = new WeakReference<>(serveReceiver);
    }
}
