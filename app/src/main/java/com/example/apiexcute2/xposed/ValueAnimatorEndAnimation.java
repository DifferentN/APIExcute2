package com.example.apiexcute2.xposed;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.example.apiexcute2.Event.AnimatorNode;
import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.dataRecord.ViewAnimationScrollRecord;
import com.example.apiexcute2.util.ProcessEventUtil;
import com.example.apiexcute2.util.ViewUtil;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class ValueAnimatorEndAnimation extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Object obj = param.thisObject;
        ViewAnimationScrollRecord record = ViewAnimationScrollRecord.getInstance();
        String viewPath = "";
//        MyEvent myEvent = MethodTrackPool.getInstance().getMyEvent();
//        if(obj instanceof ObjectAnimator){
//            Object target = ((ObjectAnimator) obj).getTarget();
//            if(target instanceof View){
//                View view = (View) target;
//                viewPath = ViewUtil.getViewPath(view);
//                ProcessEventUtil.updateEventState(viewPath, AnimatorNode.ObjectAnimatorType,myEvent);
//            }
//        }else{
//            ValueAnimator valueAnimator = (ValueAnimator) obj;
//            List<View> listView = record.getViewByValueAnimator(valueAnimator);
//            for(View view:listView){
//                viewPath = ViewUtil.getViewPath(view);
//                ProcessEventUtil.updateEventState(viewPath,AnimatorNode.ValueAnimatorType,myEvent);
//            }
//        }
    }
}
