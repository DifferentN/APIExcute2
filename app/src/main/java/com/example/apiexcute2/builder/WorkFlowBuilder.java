package com.example.apiexcute2.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.workModel.WorkItem;

public class WorkFlowBuilder {
    public static WorkItem buildWorkFlow(JSONObject jsonObject){
        JSONObject eventJSON = jsonObject.getJSONObject(WorkItem.EVENT);
        //create MyEvent by json
        MyEvent myEvent  = MyEventBuilder.buildMyEvent(eventJSON);
        WorkItem workItem = new WorkItem(myEvent);

        //attach next WorkItem to current workItem
        JSONArray nextWorkJSONArray = jsonObject.getJSONArray(WorkItem.NEXT_WORK);
        for(int i=0;i<nextWorkJSONArray.size();i++){
            JSONObject nextWorkJSONObject = nextWorkJSONArray.getJSONObject(i);
            WorkItem nextWork = buildWorkFlow(nextWorkJSONObject);
            workItem.addNextWork(nextWork);
        }
        return workItem;
    }
}
