package com.example.apiexcute2.Event;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.builder.WorkFlowBuilder;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.workModel.WorkItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MethodTrackPool {
    /**
     * 表示在onDraw时已经选择出要执行的WorkItem
     */
    public static final int SELECTED = 1;
    /**
     * 表示在onDraw时未选择出要执行的WorkItem
     * 原因：1 开始执行时
     * 2 在LocalActivityReceiver收到EXECUTE_EVENT通知后
     */
    public static final int UNSELECTED = -1;
    private Context context;
    private static volatile MethodTrackPool methodTrackPool;
    private WorkItem workFlowHead;
    private List<WorkItem> candidateWorkItems;
    private WorkItem selectedWorkItem;
    private boolean executeActionState = false; //false 表示当前操作未执行
    private static boolean isAvailable = false;
    private int selectedWorkItemState=UNSELECTED;

    public MethodTrackPool(){
        readSequence("ankiLogDetail.txt");
    }
    public static MethodTrackPool getInstance(){
        if(methodTrackPool==null){
            synchronized (MethodTrackPool.class){
                if(methodTrackPool==null){
                    methodTrackPool = new MethodTrackPool();
                }
            }
        }
        return methodTrackPool;
    }
    private void readSequence(String fileName){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        File file = new File(filePath);
        StringBuffer buf = new StringBuffer();
        try {
            if(!file.exists()){
                Log.i("LZH","序列文件不存在");
            }
            FileReader fileReader  = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while( (line = reader.readLine())!=null ){
                buf.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(buf.toString());
        workFlowHead = WorkFlowBuilder.buildWorkFlow(jsonObject);

        candidateWorkItems = new ArrayList<>();
        candidateWorkItems.add(workFlowHead);

        //设置要执行的第一个用户操作（默认操作，不需要与当前的页面状态作比较）
        selectedWorkItem = workFlowHead;
    }
    private AnimatorNode transformJSONToAnimatorNode(JSONObject jsonObject){
        String viewPath = jsonObject.getString(MyEvent.VIEW_PATH);
        AnimatorNode animatorNode = new AnimatorNode(viewPath);
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ObjectAnimatorType,
                jsonObject.getIntValue(AnimatorNode.ObjectAnimatorType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ValueAnimatorType,
                jsonObject.getIntValue(AnimatorNode.ValueAnimatorType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.AnimationType,
                jsonObject.getIntValue(AnimatorNode.AnimationType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ScrollerType,
                jsonObject.getIntValue(AnimatorNode.ScrollerType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.OverScrollerType,
                jsonObject.getIntValue(AnimatorNode.OverScrollerType));
        animatorNode.setAnimatorTypeAndNum(AnimatorNode.ViewPropertyAnimatorType,
                jsonObject.getIntValue(AnimatorNode.ViewPropertyAnimatorType));
        return animatorNode;
    }

    public static boolean isAvailable(){
        return isAvailable;
    }

    /**
     * 设置API是否可以执行
     * @param b true表示API可以执行
     */
    public void setAvailable(boolean b){
        isAvailable = b;
    }

    public void setContext(Context context){
        this.context = context;
    }

    /**
     * 获取候选操作集合
     * @return
     */
    public List<WorkItem> getCandidateWorkItems(){
        return candidateWorkItems;
    }

    /**
     * 设置将要执行的操作
     * @param workItem 表示将要执行的操作
     */
    public void setSelectedWorkItem(WorkItem workItem){
        selectedWorkItem = workItem;
        //表示selectedWorkItem还未执行
        executeActionState = false;
    }

    /**
     * 在某个用户操作执行完成后，将此操作的后继操作设置为候选操作
     * @param workItem 表示已经执行完成的用户操作
     */
    public void resetCandidateWordItem(WorkItem workItem){
        //表示selectedWorkItem 执行完成
        executeActionState = true;
        candidateWorkItems = workItem.getNextWorks();
        Log.i("LZH","workItem finish: "+System.currentTimeMillis());
    }

    /**
     * 返回将要执行的操作
     * @return
     */
    public WorkItem getSelectedWorkItem(){
        if(!executeActionState){
            return selectedWorkItem;
        }
        return null;
    }

    /**
     * 查看当前的API是否执行完成
     * @return
     */
    public boolean isAPIFinished(){
        if(candidateWorkItems==null||candidateWorkItems.isEmpty()){
            return true;
        }
        return false;
    }

    public void setSelectedWorkItemState(int selectedWorkItemState) {
        this.selectedWorkItemState = selectedWorkItemState;
    }

    public int getSelectedWorkItemState() {
        return selectedWorkItemState;
    }
}
