package com.example.apiexcute2.serve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.receive.LocalActivityReceiver;
import com.example.apiexcute2.util.MyFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.apiexcute2.receive.LocalActivityReceiver.ON_RESUME;
import static com.example.apiexcute2.receive.LocalActivityReceiver.RESUME_ACTIVITY;

public class ServeReceiver extends BroadcastReceiver {
    public static final String API_LINK = "APILINK";
    public static final String API_MODEL = "APIMODEL";
    public static final String API_OUTPUT = "APIOUTPUT";
    public static final String API_RESPONSE = "API_RESPONSE";
    public static final String PAGE_CONTENT = "PAGE_CONTENT";
    public static final String OPEN_API = "OPEN_API";
    public static final String OPEN_API_NAME = "OPEN_API_NAME";
    public static final String OPEN_ACTIVITY_NAME = "OPEN_ACTIVITY_NAME";
    public static final String OPEN_PACKAGE_NAME = "OPEN_PACKAGE_NAME";
    //API的文件名称
    private String apiName;
    //表示启动API前所需打开的页面
    private String launchActivityName;
    //表示是否启动API
    private boolean launchFlag;
    private Context context;
    //用来阻塞线程，直到得到页面信息(Output)
    private ReentrantLock lock;
    private Condition condition;
    private MyServe myServe;
    public ServeReceiver(Context context){
        this.context = context;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case ON_RESUME:
                String showActivityName = intent.getStringExtra(RESUME_ACTIVITY);
                Log.i("LZH",showActivityName);
                if(launchFlag&&launchActivityName.equals(showActivityName)){
                    Log.i("LZH","launch API");
                    launchAPI();
                    //设置启动标志，表示已经启动
                    launchFlag = false;
                }
                break;
            case API_RESPONSE:
                ArrayList<String> pageContents = intent.getStringArrayListExtra(PAGE_CONTENT);

                HashMap<String,String> pageHash = new HashMap<String,String>();
                for(String item:pageContents){
                    int lastIndex = item.lastIndexOf(":");
                    String path = item.substring(0,lastIndex);
                    String text = item.substring(lastIndex+1,item.length());
                    pageHash.put(path,text);
                }
                handleResponse(apiName,pageHash);
                break;
            case OPEN_API:
                String apiName = intent.getStringExtra(OPEN_API_NAME);
                String pkName = intent.getStringExtra(OPEN_PACKAGE_NAME);
                String activityName = intent.getStringExtra(OPEN_ACTIVITY_NAME);
                launchActivityName = activityName;
                launchFlag = true;
                executeAPI(apiName,pkName);
        }
    }
    private void launchAPI(){
        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.START_EVENT);
        context.sendBroadcast(intent);
    }
    public void executeAPI(String APIName,String packageName){
        apiName = APIName;
        //设置启动标志，表示要启动API
        launchFlag = true;
        //启动目标APP
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
        try{
            lock.lock();
            condition.await();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    private void handleResponse(final String apiName,final HashMap<String,String> pageContent){
//        AsyncTask asyncTask = new AsyncTask<String,String,JSONArray>() {
//            @Override
//            protected JSONArray doInBackground(String... path) {
//                String filePath = path[0];
//                Log.i("LZH",filePath);
//                JSONObject jsonObject = MyFileUtil.readJSONObject(filePath);
//                Log.i("LZH",jsonObject.toJSONString());
//                JSONArray userRequired = jsonObject.getJSONArray(API_OUTPUT);
//                Log.i("LZH",userRequired.toJSONString());
//                return userRequired;
//            }
//
//            @Override
//            protected void onPostExecute(JSONArray jsonArray) {
//                if(jsonArray==null){
//                    Log.i("LZH","output required is null");
//                }
//                pushOutputToUser(jsonArray,pageContent);
//            }
//        };
        String apiFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+apiName+".txt";
        JSONObject jsonObject = MyFileUtil.readJSONObject(apiFilePath);
        JSONObject userRequired = jsonObject.getJSONObject("API_OUTPUT");
        pushOutputToUser(userRequired,pageContent);
    }
    public void pushOutputToUser(JSONObject userRequired,HashMap<String,String> pageContent){
        //筛选用户需要的输出
        JSONObject responseJson = new JSONObject();
        if(userRequired!=null){
            Log.i("LZH","response to user");
            Set<String> paths = userRequired.keySet();
            for(String path:paths){
                String value = pageContent.get(path);
                if(value!=null){
                    responseJson.put(value,"");
                }
            }
            responseJson.put("success","OK");
        }else{
            //用户对页面没有要求，可以输出页success
            responseJson.put("success","OK");
        }
        myServe.setPageContent(responseJson.toJSONString());
        Log.i("LZH","set result");
        try{
            lock.lock();
            condition.signal();
            Log.i("LZH","signal");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void bindServe(MyServe myServe){
        this.myServe = myServe;
    }
    public void setLaunchActivityName(String launchActivityName) {
        this.launchActivityName = launchActivityName;
    }

    public void setLaunchFlag(boolean launchFlag) {
        this.launchFlag = launchFlag;
    }
}
