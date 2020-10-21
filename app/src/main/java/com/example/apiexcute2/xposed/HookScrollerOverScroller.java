package com.example.apiexcute2.xposed;

import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.example.apiexcute2.Event.AnimatorNode;
import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.dataRecord.ViewAnimationScrollRecord;
import com.example.apiexcute2.util.ProcessEventUtil;
import com.example.apiexcute2.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;

public class HookScrollerOverScroller extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //false代表滑动完成
        boolean isFinish = (boolean) param.getResult();
        if(isFinish){
            return;
        }
        Object obj = param.thisObject;
        ViewAnimationScrollRecord record = ViewAnimationScrollRecord.getInstance();
        View view = record.getViewByScrollOrOverScroll(obj);
        String viewPath = ViewUtil.getViewPath(view);
//        MyEvent myEvent = MethodTrackPool.getInstance().getMyEvent();
//        if(obj instanceof Scroller){
//            ProcessEventUtil.updateEventState(viewPath, AnimatorNode.ScrollerType,myEvent);
//        }else if(obj instanceof OverScroller){
//            ProcessEventUtil.updateEventState(viewPath, AnimatorNode.OverScrollerType,myEvent);
//        }

    }
}
