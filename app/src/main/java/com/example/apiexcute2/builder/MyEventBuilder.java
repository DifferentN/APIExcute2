package com.example.apiexcute2.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.eventModel.MyParameter;
import com.example.apiexcute2.model.eventModel.ViewInfo;

import java.util.ArrayList;
import java.util.List;

public class MyEventBuilder {
    public static MyEvent buildMyEvent(JSONObject jsonObject){
        String activityId = jsonObject.getString(MyEvent.ACTIVITY_ID);
        String viewID = jsonObject.getString(MyEvent.VIEW_ID);
        String viewPath = jsonObject.getString(MyEvent.VIEW_PATH);
        String methodName = jsonObject.getString(MyEvent.METHOD_NAME);
        String parameter = jsonObject.getString(MyEvent.PARAMETER_VALUE);
        String packageName = jsonObject.getString(MyEvent.PACKAGE_NAME);
        float viewX = jsonObject.getFloatValue(MyEvent.VIEW_X);
        float viewY = jsonObject.getFloatValue(MyEvent.VIEW_Y);
        float width = jsonObject.getFloatValue(MyEvent.VIEW_WIDTH);
        float height = jsonObject.getFloatValue(MyEvent.VIEW_HEIGHT);
        MyEvent myEvent = new MyEvent(activityId,viewID,viewPath,methodName,
                viewX,viewY,width,height,packageName);

        addParameter(myEvent,jsonObject);

        addStructureToMyEvent(myEvent,jsonObject);

        return myEvent;
    }

    /**
     * create structure and add toMyEvent
     * @param myEvent
     * @param jsonObject
     */
    private static void addStructureToMyEvent(MyEvent myEvent,JSONObject jsonObject){
        JSONArray structureArray = jsonObject.getJSONArray(MyEvent.STRUCTURE);
        List<ViewInfo> structure = new ArrayList<>();
        for(int i=0;i<structureArray.size();i++){
            //create ViewInfo
            JSONObject viewInfoJSON = structureArray.getJSONObject(i);
            ViewInfo viewInfo = ViewInfoBuilder.buildViewInfo(viewInfoJSON);
            structure.add(viewInfo);
        }
        myEvent.setStructure(structure);
    }
    /**
     * extract parameter and add parameter(only has type) to myEvent(setText)
     * @param myEvent
     * @param jsonObject
     */
    private static void addParameter(MyEvent myEvent,JSONObject jsonObject){
        String parameterType = jsonObject.getString(MyEvent.PARAMETER_TYPE);
        if(parameterType==null){
            return;
        }
        String parameterValue = jsonObject.getString(MyEvent.PARAMETER_VALUE);
        MyParameter myParameter = new MyParameter(parameterType,parameterValue);
        List<MyParameter> parameterList = new ArrayList<>();
        parameterList.add(myParameter);

        myEvent.setParameters(parameterList);
    }
}
