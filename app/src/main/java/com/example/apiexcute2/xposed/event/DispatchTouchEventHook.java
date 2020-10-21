package com.example.apiexcute2.xposed.event;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class DispatchTouchEventHook extends XC_MethodHook {
    private String fileName = "methodLog.txt";
    public DispatchTouchEventHook(){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        View view= (View) param.thisObject;

        View decorView = view.getRootView();
//        getViewByPath(decorView,"ss");
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            Log.i("LZH","dispatch: "+ view.getClass().getName());
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        View view= (View) param.thisObject;
//        int pos[] = new int[2];
//        view.getLocationInWindow(pos);
//        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
//        pos[0] = (int) (pos[0]/displayMetrics.density);
//        pos[1] = (int) (pos[1]/displayMetrics.density);
//        int before = pos[0];
//        int after = pos[1];
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            Log.i("LZH","dispatch: "+ view.getClass().getName()+" res: "+param.getResult());
        }

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
}
