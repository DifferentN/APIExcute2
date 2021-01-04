package com.example.apiexcute2.util;


import android.util.Log;

import com.example.apiexcute2.model.eventModel.MyEvent;
import com.example.apiexcute2.model.eventModel.ViewInfo;

import java.util.ArrayList;
import java.util.List;

public class MatchUtil {
    public static float SAME_STATE = 1.0f;
    public static float NOT_SAME_STATE = 0f;
    //assign weight of each part
    private static float p1 = 1/4f,p2 = p1 ,p3 = p1, p4 = p1;
    //the threshold of viewInfo
    private static float viewInfoThreshold = 0.6f;
    //the threshold of targetView(touch or setText view)
    private static float targetThreshold = viewInfoThreshold;
    //the threshold of structure
    public static float structureThreshold = 0.6f;
    /**
     * reckon the similarity between viewnInfo1 and viewInfo2
     * @param viewInfo1
     * @param viewInfo2
     * @return
     */
    public static float obtainViewSimilarity(ViewInfo viewInfo1,ViewInfo viewInfo2){
        float partXMin = Math.min( viewInfo1.getX(), viewInfo2.getX() );
        float partXMax = Math.max( viewInfo1.getX(), viewInfo2.getX() );
        if(partXMax==0){
            partXMax += 1;
            partXMin += 1;
        }

        float partYMin = Math.min( viewInfo1.getY(), viewInfo2.getY() );
        float partYMax = Math.max( viewInfo1.getY(), viewInfo2.getY() );
        if(partYMax==0){
            partYMin += 1;
            partYMax += 1;
        }

        float partWMin = Math.min( viewInfo1.getWidth(), viewInfo2.getWidth());
        float partWMax = Math.max( viewInfo1.getWidth(), viewInfo2.getWidth());
        if(partWMax==0){
            partWMin += 1;
            partWMax += 1;
        }

        float partHMin = Math.min( viewInfo1.getHeight(), viewInfo2.getHeight());
        float partHMax = Math.max( viewInfo1.getHeight(), viewInfo2.getHeight());
        if(partHMax==0){
            partHMin += 1;
            partHMax += 1;
        }
        float res = p1 * partXMin/partXMax  +
                p2 *  partYMin/partYMax  +
                p3 * partWMin/partWMax  +
                p4 * partHMin/partHMax;
        return res;
    }

    /**
     * reckon the similarity between MyEvent
     * 计算2个任务之间的相似度
     * @param myEvent1
     * @param myEvent2
     * @return
     */
    public static float obtainMyEventSimilarity(MyEvent myEvent1, MyEvent myEvent2){
        float activitySimilarity = myEvent1.getActivityId().equals( myEvent2.getActivityId() )?1:0;
        float userActionTypeSimilarity = myEvent1.getMethodName().equals( myEvent2.getMethodName() )?1:0;
        float targetViewSimilarity = 0;
        if(myEvent1.getPath().equals( myEvent2.getPath() )){
            targetViewSimilarity = p1 * Math.min( myEvent1.getViewX(), myEvent2.getViewX() ) / Math.max( myEvent1.getViewX(), myEvent2.getViewX() ) +
                    p2 * Math.min( myEvent1.getViewY(), myEvent2.getViewY() ) / Math.max( myEvent1.getViewY(), myEvent2.getViewY() ) +
                    p3 * Math.min( myEvent1.getWidth(), myEvent2.getWidth()) / Math.max( myEvent1.getWidth(), myEvent2.getWidth()) +
                    p4 * Math.min( myEvent1.getHeight(), myEvent2.getHeight()) / Math.max( myEvent1.getHeight(), myEvent2.getHeight());
        }
        float structureSimilarity = obtainStructureSimilarity(myEvent1.getStructure(),myEvent2.getStructure());
        if(activitySimilarity==1 && userActionTypeSimilarity==1 &&
            targetViewSimilarity>targetThreshold && structureSimilarity>structureThreshold){
            return SAME_STATE;
        }
        return NOT_SAME_STATE;
    }

    public static float obtainStructureSimilarity(List<ViewInfo> structure1,List<ViewInfo> structure2){
        float layer1 = getStructureLayer(structure1);
        float layer2 = getStructureLayer(structure2);
        float maxLayer = Math.max(layer1, layer2 );

        float viewNum1 = getViewNum(structure1);
        float viewNum2 = getViewNum(structure2);
        float maxNum = Math.max(viewNum1,viewNum2);
        int sameNum = reckonViewTreeSimilarity( structure1, structure2 );
        float part1 = Math.min(layer1,layer2)/maxLayer*p1;
        float part2 = Math.min(viewNum1,viewNum2)/maxNum*p1;
        float part3 = sameNum/(viewNum1+viewNum2-sameNum)*p3;
        float res = part1 + part2 + part3 + p4;
        float ss = sameNum/Math.min(viewNum1,viewNum2);
        if(ss<0.46){
            Log.i("LZH","same: "+sameNum+" ss: "+ss+" res: "+res);
            ss = -1;
            res = -1;
        }
        Log.i("LZH","viewTree Similarity: "+ss+" num1: "+viewNum1+" num2: "+viewNum2);
        return res;

    }
    public static int reckonViewTreeSimilarity(List<ViewInfo> viewInfos1,List<ViewInfo> viewInfos2){
        //the threshold of viewInfo similarity
        float w = viewInfoThreshold;
        int res = 0;
        if(viewInfos1==null){
            return res;
        }
        //reckon the number of viewInfos which similarity bigger than w
        //获取 相似度大于阈值w的 元素（视图）对的数量
        for(ViewInfo viewInfo:viewInfos1){
//            Log.i("LZH","src: "+viewInfo.getViewName()+" "+viewInfo.getViewIndex());
            List<ViewInfo> matchedViewInfoList = searchMatchedViewInfo(viewInfo,viewInfos2);
            //不是同一个View，相似度为0
            if(matchedViewInfoList==null){
                Log.i("LZH","match view is null");
                continue;
            }
            int maxNum = 0;
            for(ViewInfo matchViewInfo:matchedViewInfoList){
                float similarity = obtainViewSimilarity(viewInfo,matchViewInfo);
//                Log.i("LZH","check: "+similarity);
                int temp = 0;
                //view对的相似度大于阈值，res加一
                if(similarity>=w){
//                    res++;
                    temp = 1;
                }
//                if(viewInfo.getChilds()==null){
//                    Log.i("LZH","viewInfo child is null");
//                }else {
//                    Log.i("LZH","child1 size: "+viewInfo.getChilds().size());
//                    Log.i("LZH","child2 size: "+matchViewInfo.getChilds().size());
//                }
                //计算子视图中 相似度大于阈值的元素对的个数
                temp += reckonViewTreeSimilarity(viewInfo.getChilds(), matchViewInfo.getChilds());
//                Log.i("LZH","temp: "+temp);
                maxNum = Math.max(maxNum,temp);
            }
            res += maxNum;
        }
        return res;
    }

    /**
     * 在childs中搜索与childViewInfo相匹配的ViewInfo
     * 判断依据：名称和序号
     * @param childViewInfo
     * @param childs
     * @return
     */
    private static List<ViewInfo> searchMatchedViewInfo(ViewInfo childViewInfo, List<ViewInfo> childs){
        if(childs==null){
            Log.i("LZH","view child is null");
            return null;
        }
        List<ViewInfo> matchedList = new ArrayList<>();
        for(ViewInfo viewInfo:childs){
//            Log.i("LZH","prepare: "+viewInfo.getViewName()+" "+viewInfo.getViewIndex());
            if( viewInfo.getViewName().equals(childViewInfo.getViewName()) &&
                viewInfo.getViewIndex()==childViewInfo.getViewIndex()){
                matchedList.add(viewInfo);
            }
//            if( viewInfo.getViewName().equals(childViewInfo.getViewName()) ){
//                matchedList.add(viewInfo);
//            }
        }
        return matchedList;
    }
    private static int getViewNum(List<ViewInfo> structure){
        List<ViewInfo> queue = new ArrayList<>();
        queue.addAll(structure);
        int num = 0;
        while(!queue.isEmpty()){
            ViewInfo viewInfo = queue.remove(0);
            num++;
            List<ViewInfo> childViewInfos = viewInfo.getChilds();
            if(childViewInfos != null){
                queue.addAll(childViewInfos);
            }
        }
        return num;
    }

    /**
     * obtain the height of the window structure
     * @param structure
     * @return
     */
    private static int getStructureLayer(List<ViewInfo> structure){
        int res = 0;
        for(ViewInfo viewInfo:structure){
            res = Math.max(res,getViewTreeLayer(viewInfo));
        }
        return res;
    }

    /**
     * obtain the height of the view
     * @param viewInfo represent a view
     * @return
     */
    private static int getViewTreeLayer(ViewInfo viewInfo){
        if(viewInfo.getChilds()==null){
            return 1;
        }
        int res = 1;
        List<ViewInfo> childs = viewInfo.getChilds();
        for(ViewInfo child:childs){
            res = Math.max( res, getViewTreeLayer(child) + 1 );
        }
        return res;
    }
}
