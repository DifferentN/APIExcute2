package com.example.apiexcute2.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewUtil {
    public static int getViewIndex(View target){
        View decorView = target.getRootView();
        ArrayList<View> list = new ArrayList<>();
        list.add(decorView);
        ViewGroup viewGroup;
        View view;
        int index = 0;
        while(!list.isEmpty()){
            view = list.remove(0);
            index++;
            if(view==target){
                return index;
            }else if(view instanceof ViewGroup){
                viewGroup = (ViewGroup) view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    view = viewGroup.getChildAt(i);
                    list.add(view);
                }
            }
        }
        return -1;
    }
    public static String getViewPath(View view){
        if(view==null){
            return "";
        }
        View decorView = view.getRootView();
        ViewGroup viewGroup;
        View child;
        ViewNode childNode;
        ViewNode temp;
        List<ViewNode> list = new ArrayList<>();
        list.add(new ViewNode(decorView,decorView.getClass().getName()));
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.view==view){
                return temp.path;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    childNode = new ViewNode(child,temp.path+"/"+child.getClass()+":"+i);
                    list.add(childNode);
                }
            }
        }
        return "";
    }
    public static String getActivityNameByView(View view){
        Context context = null;
        String activityName = "";
        if(view!=null){
            context = view.getContext();
            while(context instanceof ContextWrapper){
                if(context instanceof Activity){
                    activityName = ((Activity)context).getComponentName().getClassName();
                    break;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
        }
        return activityName;
    }
    static class ViewNode{
        public View view;
        public String path;
        public ViewNode(View view,String path){
            this.view = view;
            this.path = path;
        }
    }
}
