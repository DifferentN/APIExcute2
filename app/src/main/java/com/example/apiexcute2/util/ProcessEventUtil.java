package com.example.apiexcute2.util;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.apiexcute2.Event.AnimatorNode;
import com.example.apiexcute2.model.eventModel.MyEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ProcessEventUtil {

    /**
     * 检查此view的动画是否执行完毕
     * @param animatorNode
     * @return true表示执行完毕
     */
    public static boolean checkAnimatorViewState(AnimatorNode animatorNode){
        if(animatorNode.getAnimatorTime(AnimatorNode.ObjectAnimatorType)<=0&&
                animatorNode.getAnimatorTime(AnimatorNode.ValueAnimatorType)<=0&&
                animatorNode.getAnimatorTime(AnimatorNode.AnimationType)<=0&&
                animatorNode.getAnimatorTime(AnimatorNode.ScrollerType)<=0&&
                animatorNode.getAnimatorTime(AnimatorNode.OverScrollerType)<=0&&
                animatorNode.getAnimatorTime(AnimatorNode.ValueAnimatorType)<=0){
            return true;
        }
        return false;
    }
    public static boolean checkViewPos(View view,MyEvent myEvent){
        if(!tranversCheck(view)){
            return false;
        }
        int pos[] = new int[2];
        view.getLocationInWindow(pos);
        float dpPos[] = new float[2];
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        dpPos[0] = (pos[0]/displayMetrics.density);
        dpPos[1] = (pos[1]/displayMetrics.density);
        Log.i("LZH","check pos");
        if(dpPos[0]==myEvent.getViewX()&&dpPos[1]==myEvent.getViewY()){
            Log.i("LZH","pos pass");
            return true;
        }else{
            Log.i("LZH","curX: "+pos[0]+" curY: "+pos[1]+" x: "+myEvent.getViewX()+" y: "+myEvent.getViewY());
            return false;
        }
    }
    public static boolean tranversCheck(View view){
        ViewParent viewParent = view.getParent();
        boolean res = true;
        while(viewParent!=null){
            if(viewParent instanceof ViewGroup){
                if(!checkCVRPE((View) viewParent)){
                    res = false;
                    break;
                }
            }
            viewParent = viewParent.getParent();
        }
        return res;
    }
    public static boolean checkCVRPE(View view){
        if(!(view instanceof ViewGroup)){
            return true;
        }
        Class clazz = ViewGroup.class;
        boolean res = false;
        try {
            Method method = clazz.getDeclaredMethod("canViewReceivePointerEvents",View.class);
            method.setAccessible(true);
            res = (boolean) method.invoke(view,view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }
}
