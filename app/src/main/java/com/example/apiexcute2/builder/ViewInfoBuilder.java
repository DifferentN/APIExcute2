package com.example.apiexcute2.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.eventModel.ViewInfo;

import java.util.ArrayList;
import java.util.List;

public class ViewInfoBuilder {
    public static ViewInfo buildViewInfo(JSONObject jsonObject){
        float viewX = jsonObject.getFloat(MyEvent.VIEW_X);
        float viewY = jsonObject.getFloat(MyEvent.VIEW_Y);
        float viewWidth = jsonObject.getFloat(MyEvent.VIEW_WIDTH);
        float viewHeight = jsonObject.getFloat(MyEvent.VIEW_HEIGHT);
        String viewName = jsonObject.getString(MyEvent.VIEW_CLASS_NAME);
        int viewIndex = jsonObject.getIntValue(MyEvent.VIEW_CHILD_INDEX);
        //避免使用xpath
        ViewInfo viewInfo = new ViewInfo(viewX,viewY,viewWidth,viewHeight,
                "",viewName,viewIndex);

        JSONArray childJSONArray = jsonObject.getJSONArray(MyEvent.CHILDS);
        if(childJSONArray!=null){
            List<ViewInfo> childViewInfos = new ArrayList<>();
            for(int i=0;i<childJSONArray.size();i++){
                JSONObject viewInfoJSONObject = childJSONArray.getJSONObject(i);
                ViewInfo childViewInfo = buildViewInfo(viewInfoJSONObject);
                childViewInfos.add(childViewInfo);
            }
            viewInfo.setChilds(childViewInfos);
        }
        return viewInfo;
    }
}
