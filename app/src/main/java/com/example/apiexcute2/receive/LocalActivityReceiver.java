package com.example.apiexcute2.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.workModel.WorkItem;
import com.example.apiexcute2.monitor.OnDrawMonitor;
import com.example.apiexcute2.util.ActivityUtil;
import com.example.apiexcute2.util.ViewUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.example.apiexcute2.serve.ServeReceiver.API_RESPONSE;
import static com.example.apiexcute2.serve.ServeReceiver.PAGE_CONTENT;

/**
 * 主要用来播放Intent，输入，点击事件。
 * 抽取，传递页面信息
 */
public class LocalActivityReceiver extends BroadcastReceiver{
    private Activity selfActivity;

    public static final String START_EVENT = "START_EVENT";
    public static final String ON_RESUME = "ON_RESUME";
    public static final String RESUME_ACTIVITY = "RESUME_ACTIVITY";
    public static final String EXECUTE_EVENT = "EXECUTE_EVENT";
    public static final String CAPTURE_PAGE_CONTENT = "CAPTURE_PAGE_CONTENT";

    private String selfActivityName = "";
    private String showActivityName = "";
    private String selfPackageName;
    private MyEvent myEvent;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        FrameLayout frameLayout;
        MethodTrackPool methodTrackPool = null;
        WorkItem workItem = null;
        OnDrawMonitor onDrawMonitor = null;
        switch (action){
            case ON_RESUME:
                showActivityName = intent.getStringExtra(RESUME_ACTIVITY);
                ActivityUtil.setCurActivityName(showActivityName);
//                Log.i("LZH","show Activity finish: "+System.currentTimeMillis());
//                Log.i("LZH","slefActivity"+selfActivityName+" Current ActivityName resume "+showActivityName);
                break;
            case START_EVENT:
                if(!showActivityName.equals(selfActivityName)){
                    return;
                }
                methodTrackPool = MethodTrackPool.getInstance();
                methodTrackPool.setAvailable(true);
                onDrawMonitor = OnDrawMonitor.getInstance();
                onDrawMonitor.setAPIStartFlag(true);
                workItem = methodTrackPool.getSelectedWorkItem();
                myEvent = workItem.getMyEvent();

                Log.i("LZH",selfActivityName+"\n"+showActivityName+"\n"+myEvent.getActivityId());
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(myEvent.getActivityId())){
                    break;
                }
                Log.i("LZH","start Event");
//                if( executeUserAction(myEvent) ){
//                    //通知methodTrackPool 当前发过来的workItem已经完成
//                    methodTrackPool.resetCandidateWordItem(workItem);
//                }
                break;
            case LocalActivityReceiver.EXECUTE_EVENT:
                boolean isAvailable = MethodTrackPool.isAvailable();
                Log.i("LZH","state: "+isAvailable);
                if(!isAvailable){
                    break;
                }
                Log.i("LZH","MethodTrackPool is available");
                methodTrackPool = MethodTrackPool.getInstance();
                workItem = methodTrackPool.getSelectedWorkItem();
                if(workItem==null){
                    break;
                }
                myEvent = workItem.getMyEvent();
                String topActivityName = ActivityUtil.getTopActivityName(selfActivity.getApplicationContext());
//                if(!topActivityName.equals(showActivityName)){
//                    showActivityName = topActivityName;
//                    Log.i("LZH","change topActivityName");
//                }
                Log.i("LZH",selfActivityName+"\n"+showActivityName+"\n"+myEvent.getActivityId());
                //有当前页面检查
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(myEvent.getActivityId())){
                    break;
                }


//                //针对食谱大全进行修改测试 测试可以
//                if(!selfActivityName.contains(myEvent.getActivityId())){
//                    break;
//                }


//                if(!selfActivityName.contains(myEvent.getActivityId())){
//                    break;
//                }

                if(executeUserAction(myEvent)){
                    //通知methodTrackPool 当前发过来的workItem已经完成
                    methodTrackPool.resetCandidateWordItem(workItem);
                    onDrawMonitor = OnDrawMonitor.getInstance();
                    onDrawMonitor.sendOnDraw(selfActivity.getWindow().getDecorView());
                }
                //选择的workItem执行过了，重置状态
                Log.i("LZH","reset workItem state");
                methodTrackPool.setSelectedWorkItemState(MethodTrackPool.UNSELECTED);
                break;
            case CAPTURE_PAGE_CONTENT:
                if(selfActivityName.equals(showActivityName)){
                    //获取用户屏幕内容
                    ArrayList<String> contents = ViewUtil.capturePageContent(selfActivity);
                    //将屏幕内容发送到MyServe
                    Intent pageResponse = new Intent();
                    pageResponse.setAction(API_RESPONSE);
                    pageResponse.putExtra(PAGE_CONTENT,contents);
                    selfActivity.sendBroadcast(pageResponse);
                }
                break;
        }
    }
    private boolean executeUserAction(MyEvent myEvent){
        boolean executionOver = false;
        Log.i("LZH","imitate user action "+myEvent.getMethodName());
        if(myEvent.getMethodName().equals(MyEvent.SETTEXT)){
            TextView textView = null;
//            textView = (TextView) getViewByPath2(myEvent.getPath());
            textView = (TextView) ViewUtil.getViewByPath2(myEvent.getPath(),selfActivity.getApplicationContext());
            if(textView==null){
                textView = (TextView) ViewUtil.findByViewCoordinate(selfActivity.getWindow().getDecorView(),
                        myEvent.getPath(),myEvent.getViewX(),myEvent.getViewY(),
                        myEvent.getWidth(),myEvent.getHeight());
            }
            if(textView==null){
                textView = selfActivity.findViewById(Integer.valueOf(myEvent.getComponentId()));
            }
            if(textView==null||!ViewUtil.isVisible(textView)){
                Log.i("LZH","textView is null:setText");
                return executionOver;
            }
            Log.i("LZH","setText: "+myEvent.getParameterValue());
            textView.setText(myEvent.getParameterValue());
            executionOver = true;
        }else if(myEvent.getMethodName().equals(MyEvent.DISPATCH)){
            View view = ViewUtil.getViewByPath(selfActivity.getWindow().getDecorView().getRootView(),
                    myEvent.getPath());
            if(view==null){
                //可能会存在多个相同路径的view
                Log.i("LZH","find by getPath2");
                view = ViewUtil.getViewByPath2(myEvent.getPath(),selfActivity.getApplicationContext());
            }
            if(view==null){
                view = ViewUtil.findByViewCoordinate(selfActivity.getWindow().getDecorView(),
                        myEvent.getPath(),myEvent.getViewX(),myEvent.getViewY(),
                        myEvent.getWidth(),myEvent.getHeight());
            }
            if(view==null){
                view = selfActivity.findViewById(Integer.valueOf(myEvent.getComponentId()));
            }
            if(view!=null){
                Log.i("LZH","view Width: "+view.getWidth()+" height: "+view.getHeight());
            }
//            if(view==null&&userAction.getViewId()>0){
//                Log.i("LZH","can't get view by viewPath; "+userAction.getViewPath());
////                view = selfActivity.findViewById(userAction.getViewId());
//            }
            if(view==null||view.getWidth()==0||view.getHeight()==0||
                    !ViewUtil.isVisible(view)){
                Log.i("LZH","view is null:dispatchTouchEvent");
                return executionOver;
            }
            Log.i("LZH","findById: "+view.getId()+"w: "+view.getWidth()+"h: "+view.getHeight());
            Log.i("LZH","click view");
            imitateClick(view);
            Log.i("LZH","activated: "+view.isActivated()+
                    "clickable: "+view.isClickable()+
                    "focusable: "+view.isFocusable());

            executionOver = true;
//            checkFrame(selfActivity.getWindow().getDecorView().getRootView(),
//                    myEvent.getPath());
        }
        if(executionOver){
            if(MethodTrackPool.getInstance().isAPIFinished()){
                //API 中的用户事件已经执行完成，发送一个延时广播，去捕获用户页面中的内容
                selfActivity.getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setAction(LocalActivityReceiver.CAPTURE_PAGE_CONTENT);
                        selfActivity.sendBroadcast(intent);
                    }
                },1000);
            }
        }
        return executionOver;
    }
    private void imitateClick(View view){
        int clickPos[] = new int[2];
        view.getLocationInWindow(clickPos);
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Log.i("LZH","rectX: "+rect.left+" rectY: "+rect.top);
//        clickPos[0]+=view.getWidth()/2;
//        clickPos[1]+=view.getHeight()/2;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = clickPos[0];
        int y = clickPos[1];
        Log.i("LZH","x: "+x+" y: "+y);
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
//        selfActivity.dispatchTouchEvent(motionEvent);
        view.getRootView().dispatchTouchEvent(motionEvent);
//        view.dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
//        view.dispatchTouchEvent(motionEvent);
//        selfActivity.dispatchTouchEvent(motionEvent);
        view.getRootView().dispatchTouchEvent(motionEvent);
    }
}
