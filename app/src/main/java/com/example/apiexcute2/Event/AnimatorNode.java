package com.example.apiexcute2.Event;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.apiexcute2.model.eventModel.MyEvent.VIEW_PATH;

public class AnimatorNode {
    public static final String ObjectAnimatorType = "ObjectAnimator";
    public static final String ValueAnimatorType = "ValueAnimatorType";
    public static final String AnimationType = "AnimationType";
    public static final String ScrollerType = "ScrollerType";
    public static final String OverScrollerType = "OverScroller";
    public static final String ViewPropertyAnimatorType = "ViewPropertyAnimator";
    private HashMap<String,Integer> hash;
    private String viewPath;
    public AnimatorNode(String path){
        viewPath = path;
        hash = new HashMap<>();
        hash.put(ObjectAnimatorType,0);
        hash.put(ValueAnimatorType,0);
        hash.put(AnimationType,0);
        hash.put(ScrollerType,0);
        hash.put(OverScrollerType,0);
        hash.put(ViewPropertyAnimatorType,0);
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setAnimatorTypeAndNum(String animationType,int num){
        hash.put(animationType,num);
    }
    public void addAnimator(String animationType){
        hash.put(animationType,hash.get(animationType)+1);
    }
    public void reduceAnimator(String animationType){
        hash.put(animationType,hash.get(animationType)-1);
    }
    public int getAnimatorTime(String animationType){
        return hash.get(animationType);
    }
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        Set<Map.Entry<String,Integer>> entrySet =hash.entrySet();
        for(Map.Entry<String,Integer> entry:entrySet){
            jsonObject.put(entry.getKey(),entry.getValue());
        }
        jsonObject.put(VIEW_PATH,viewPath);
        return jsonObject;
    }
}
