package com.example.apiexcute2.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.Event.MyEvent;
import com.example.apiexcute2.util.ActivityUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        switch (action){
            case ON_RESUME:
                showActivityName = intent.getStringExtra(RESUME_ACTIVITY);
                ActivityUtil.setCurActivityName(showActivityName);
                break;
            case START_EVENT:
                if(!showActivityName.equals(selfActivityName)){
                    return;
                }
                MethodTrackPool.getInstance().setAvailable(true);
                myEvent = MethodTrackPool.getInstance().getMyEvent();

                Log.i("LZH",selfActivityName+"\n"+showActivityName+"\n"+myEvent.getActivityId());
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(myEvent.getActivityId())){
                    break;
                }
                Log.i("LZH","start Event");
                if(executeUserAction(myEvent)){
                }
                break;
            case LocalActivityReceiver.EXECUTE_EVENT:
                boolean isAvailable = MethodTrackPool.isAvailable();
                if(!isAvailable){
                    break;
                }
                myEvent = MethodTrackPool.getInstance().getMyEvent();
                if(myEvent==null){
                    break;
                }
//                Log.i("LZH",selfActivityName+"\n"+showActivityName+"\n"+userAction.getActivityName());
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(myEvent.getActivityId())){
                    break;
                }
                if(executeUserAction(myEvent)){
                }
                break;
        }
    }
    private boolean executeUserAction(MyEvent myEvent){
        boolean executionOver = false;
        Log.i("LZH","imitate user action "+myEvent.getMethodName());
        if(myEvent.getMethodName().equals(MyEvent.SETTEXT)){
            TextView textView = null;
            textView = (TextView) getViewByPath2(myEvent.getPath());
            if(textView==null){
                textView = selfActivity.findViewById(Integer.valueOf(myEvent.getComponentId()));
            }
            if(textView==null){
                Log.i("LZH","textView is null:setText");
                return executionOver;
            }
            Log.i("LZH","setText");
            textView.setText(myEvent.getParameter());
            executionOver = true;
        }else if(myEvent.getMethodName().equals(MyEvent.DISPATCH)){
            View view = getViewByPath(selfActivity.getWindow().getDecorView().getRootView(),
                    myEvent.getPath());
            if(view==null){
                //可能会存在多个相同路径的view
                Log.i("LZH","find by getPath2");
                view = getViewByPath2(myEvent.getPath());
            }
            if(view!=null){
                Log.i("LZH","view Width: "+view.getWidth()+" height: "+view.getHeight());
            }
//            if(view==null&&userAction.getViewId()>0){
//                Log.i("LZH","can't get view by viewPath; "+userAction.getViewPath());
////                view = selfActivity.findViewById(userAction.getViewId());
//            }
            if(view==null||view.getWidth()==0||view.getHeight()==0){
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
            //通知methodTrackPool 当前发过来的event已经完成
            MethodTrackPool.getInstance().finishCurEvent();
        }
        return executionOver;
    }
    private void imitateClick(View view){
        int clickPos[] = new int[2];
        view.getLocationInWindow(clickPos);
        clickPos[0]+=view.getWidth()/2;
        clickPos[1]+=view.getHeight()/2;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = clickPos[0];
        int y = clickPos[1];
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        selfActivity.dispatchTouchEvent(motionEvent);
//        view.getRootView().dispatchTouchEvent(motionEvent);
//        view.dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
//        view.dispatchTouchEvent(motionEvent);
        selfActivity.dispatchTouchEvent(motionEvent);
//        view.getRootView().dispatchTouchEvent(motionEvent);
    }
    private View getViewByPath2(String path){
        Object windowManagerImpl = selfActivity.getSystemService(Context.WINDOW_SERVICE);
        Class windManagerImplClazz = windowManagerImpl.getClass();
        Object windowManagerGlobal = null;
        Class windManagaerGlobalClass = null;
        ArrayList<View> mViews = null;
        try {
            Field field = windManagerImplClazz.getDeclaredField("mGlobal");
            field.setAccessible(true);
            windowManagerGlobal = field.get(windowManagerImpl);
            windManagaerGlobalClass = windowManagerGlobal.getClass();
            field = windManagaerGlobalClass.getDeclaredField("mViews");
            field.setAccessible(true);
            mViews = (ArrayList<View>) field.get(windowManagerGlobal);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        View targetView = null;
        if(mViews!=null){
            for(View view:mViews){
                targetView = getViewByPath(view,path);
                checkFrame(view,path);
                if(targetView!=null){
                    Log.i("LZH","find view");
//                    return targetView;
                }
            }
            return targetView;
        }
        return null;
    }
    private View getViewByPath(View rootView,String viewPath){
        class Node{
            public String path;
            public View view;
            public Node(String path,View view){
                this.path = path;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = rootView;
        String path = decorView.getClass().getName();
        queue.add(new Node(path,decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
//            Log.i("LZH","path: "+temp.path);
            if(temp.path.equals(viewPath)){
                if(temp.view instanceof TextView){
                    Log.i("LZH","text: "+((TextView) temp.view).getText());
                }
                return temp.view;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    queue.add(new Node(temp.path+"/"+child.getClass()+":"+i,child));
                }
            }
        }
        return null;
    }
    private View checkFrame(View rootView,String viewPath){
        class Node{
            public String path;
            public View view;
            public Node(String path,View view){
                this.path = path;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = rootView;
        String path = decorView.getClass().getName();
        queue.add(new Node(path,decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
//            Log.i("LZH","path: "+temp.path);
            if(temp.path.equals(viewPath)){
                if(temp.view instanceof TextView){
                    Log.i("LZH","text: "+((TextView) temp.view).getText());
                }
                return temp.view;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    queue.add(new Node(temp.path+"/"+child.getClass()+":"+i,child));
                }
                if(temp.view instanceof FrameLayout){
                    Log.i("LZH","frameLayout: "+temp.path);
                    for(int i=0;i<viewGroup.getChildCount();i++){
                        child = viewGroup.getChildAt(i);
                        Log.i("LZH","frameChild: "+child.getClass().getName()+" "+child.getWidth()+" "+child.getHeight());
                    }
                }
            }
        }
        return null;
    }
}
