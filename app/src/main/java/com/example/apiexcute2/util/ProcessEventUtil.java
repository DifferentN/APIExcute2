package com.example.apiexcute2.util;

import com.example.apiexcute2.Event.AnimatorNode;
import com.example.apiexcute2.Event.MyEvent;

import java.util.List;

public class ProcessEventUtil {
    /**
     * 检查event的动画是否全部执行完毕
     * @param myEvent
     * @return true表示已全部执行完毕
     */
    public static boolean checkEventState(MyEvent myEvent){
        if(myEvent.getSnapshot().isEmpty()){
            return true;
        }
        return false;
    }

    /**
     *  某个view的动画结束时，对当前Event的状态进行更新
     * @param viewPath
     * @param animatorType
     */
    public static void updateEventState(String viewPath,String animatorType,MyEvent myEvent){
        if(myEvent==null){
            return;
        }
        if(!myEvent.getPath().contains(viewPath)){
            return;
        }
        List<AnimatorNode> snapshot = myEvent.getSnapshot();
        for(AnimatorNode animatorNode:snapshot){
            if(animatorNode.getViewPath().equals(viewPath)){
                animatorNode.reduceAnimator(animatorType);
            }
        }
        for(int i=snapshot.size()-1;i>=0;i--){
            if(checkAnimatorViewState(snapshot.get(i))){
                snapshot.remove(i);
            }
        }
    }

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
}
