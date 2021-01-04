 package com.example.apiexcute2.monitor;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.apiexcute2.Event.MethodTrackPool;
import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.eventModel.ViewInfo;
import com.example.apiexcute2.model.workModel.WorkItem;
import com.example.apiexcute2.receive.LocalActivityReceiver;
import com.example.apiexcute2.util.ActivityUtil;
import com.example.apiexcute2.util.MatchUtil;
import com.example.apiexcute2.util.ViewUtil;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class OnDrawMonitor {
    private static String TAG = "OnDrawMonitor";
    private static volatile OnDrawMonitor onDrawMonitor;
    //onDraw调用时设置为true
    private boolean drawFlag = false;
    //执行相似度检查任务线程
    private FutureTask<Object> curTask;
    //最近调用onDraw的视图
    private View view;
    //表示是否要开始检查视图界面
    private boolean APIStartFlag ;
    //前一时刻的页面状态
    private List<ViewInfo> preWindowStructure;
    public static OnDrawMonitor getInstance(){
        if(onDrawMonitor==null){
            synchronized (OnDrawMonitor.class){
                if(onDrawMonitor==null){
                    onDrawMonitor = new OnDrawMonitor();
                }
            }
        }
        return onDrawMonitor;
    }

    public void setAPIStartFlag(boolean APIStartFlag) {
        this.APIStartFlag = APIStartFlag;
    }

    public void sendOnDraw(View view){
        if(!APIStartFlag){
            return;
        }
        drawFlag = true;
        this.view = view;
        //确保当前只有一个界面相似度检查任务
        if(curTask==null||curTask.isDone()){
            launchTask();
        }
    }

    /**
     * 启动一个界面相似度检查任务
     */
    private void launchTask(){
        Log.i("LZH","launch task");
        curTask = createTask();
        startTask(curTask);
    }
    private FutureTask<Object> createTask(){
        FutureTask<Object> futureTask = new FutureTask<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //先睡眠18ms
                Thread.sleep(72);
                //检查视图结构相似性
//                Log.i("LZH","obtaining workItem" );
                WorkItem workItem = obtainSameWorkItem();
                if(workItem!=null){
                    sendWorkItem(workItem);
                }else{
                    //上一个workItem还未执行完，或者没有找到匹配的workItem
//                    Log.i("LZH","workItem is null");
                    //表示上一个任务未完成
                    MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
                    if(methodTrackPool.getSelectedWorkItemState()==MethodTrackPool.SELECTED){
                        Log.i("LZH","last work Item not finish");
                        return null;
                    }
                    Log.i("LZH","not find match workItem");
                    //表示未找到匹配的workItem
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            //在界面结构相似度计算完成之后，如果还有onDraw方法被调用（drawFlag被设置为true）
                            //则再次进行界面结构相似度检查
                            if(drawFlag){
                                drawFlag = false;
                                if(curTask.isDone()){
                                    launchTask();
                                }
                            }
                        }
                    });
                }
                return null;
            }
        });
        return futureTask;
    }
    private void startTask(final FutureTask<Object> futureTask){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean same = false;
                while(!same){
                    Log.i(TAG,"start get curWindow");
                    List<ViewInfo> curWindowStructure = ViewUtil.obtainStructureOfWindow(view.getContext());
                    if(preWindowStructure==null){
                        Log.i("TAG","preWindow is null");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Log.i(TAG,"reckon sim");
                        float sim = MatchUtil.obtainStructureSimilarity(curWindowStructure,
                                preWindowStructure);
                        Log.i(TAG,"sim is "+sim);
                        if(sim>0.92f){
                            same = true;
                        }else {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    preWindowStructure = curWindowStructure;
                }
                Log.i(TAG,"start futureTask");
                new Thread(futureTask).start();
            }
        }).start();

    }

    /**
     * 获取一个要执行的任务，如果上次的任务还未执行，则返回null
     * @return
     */
    private WorkItem obtainSameWorkItem(){
        Activity activity = ActivityUtil.getActivity(view);

        MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
        if(methodTrackPool.getSelectedWorkItemState()==MethodTrackPool.SELECTED){
            return null;
        }

        List<ViewInfo> structure = ViewUtil.obtainStructureOfWindow(view.getContext());

        List<WorkItem> candidateWorkItem = methodTrackPool.getCandidateWorkItems();

        WorkItem selectedWorkItem = selectedSameWorkItem(activity,structure,candidateWorkItem);

        return selectedWorkItem;
    }

    private WorkItem selectedSameWorkItem(Activity activity, List<ViewInfo> structure, List<WorkItem> candidateWorkItem) {
        WorkItem targetWorkItem = null;
        float maxSimilarity = 0;

        for(WorkItem workItem:candidateWorkItem){
            MyEvent myEvent = workItem.getMyEvent();
            Log.i("LZH","activity name: "+myEvent.getActivityId()+" "+activity.getClass().getName());
            //检查MyEvent的Activity类名是否与当前显示的Activity的类名相同
            if( !myEvent.getActivityId().equals( activity.getClass().getName() ) ){
                continue;
            }

            float similarity = MatchUtil.obtainStructureSimilarity(myEvent.getStructure(),
                    structure);
            Log.i("LZH","similarity: "+similarity+"");
            if( similarity >= MatchUtil.structureThreshold){
                if(similarity>maxSimilarity){
                    maxSimilarity = similarity;
                    targetWorkItem = workItem;
                }
            }
        }
        return targetWorkItem;
    }
    private void sendWorkItem(WorkItem workItem){
        MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
        final Activity activity = ActivityUtil.getActivity(view);

        methodTrackPool.setSelectedWorkItem(workItem);
//        Log.i("LZH","set selected");
        methodTrackPool.setSelectedWorkItemState(MethodTrackPool.SELECTED);
        //掌上公交的view无法提交线程
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.i("LZH","send start flag");
                Intent intent = new Intent();
                intent.setAction(LocalActivityReceiver.EXECUTE_EVENT);
                activity.sendBroadcast(intent);
            }
        },200);
    }
}
