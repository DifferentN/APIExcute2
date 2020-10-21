package com.example.apiexcute2.xposed;

import android.view.View;

import com.example.apiexcute2.Event.AnimatorNode;
import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.util.ProcessEventUtil;
import com.example.apiexcute2.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;

public class ViewApplyLegacyAnimation extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //false 表示动画已经完成
        boolean isFinish = (boolean) param.getResult();
        if(!isFinish){
            View view = (View) param.thisObject;
            String viewPath = ViewUtil.getViewPath(view);
//            MyEvent myEvent = MethodTrackPool.getInstance().getMyEvent();
//            ProcessEventUtil.updateEventState(viewPath, AnimatorNode.AnimationType,myEvent);

        }
    }
}
