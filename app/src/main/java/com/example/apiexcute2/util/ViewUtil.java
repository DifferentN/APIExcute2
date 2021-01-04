package com.example.apiexcute2.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexcute2.model.eventModel.ViewInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public static int getViewNum(View rootView){
        List<View> queue = new ArrayList<>();
        queue.add(rootView);
        int num = 0;
        while(!queue.isEmpty()){
            num++;
            View curView = queue.remove(0);
            if(curView instanceof ViewGroup){
                ViewGroup vg = (ViewGroup) curView;
                for(int i=0;i<vg.getChildCount();i++){
                    queue.add(vg.getChildAt(i));
                }
            }
        }
        return num;
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
                List<View> childViews = obtainChildViews(viewGroup);
                for(int i=0;i<childViews.size();i++){
                    child = childViews.get(i);
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
    public static boolean isVisible(View view){
        while(view!=null){
            if(view.getVisibility()==View.INVISIBLE||
                    view.getVisibility()==View.GONE||view.getAlpha()==0){
                return  false;
            }
            ViewParent parent = view.getParent();
            if(parent instanceof View){
                view = (View) parent;
            }else view = null;
        }
        return true;
    }
    public static View getViewByPath2(String path,Context context){
        Object windowManagerImpl = context.getSystemService(Context.WINDOW_SERVICE);
        Class windManagerImplClazz = windowManagerImpl.getClass();
        Object windowManagerGlobal = null;
        Class windManagaerGlobalClass = null;
        ArrayList<View> mViews = null;
        try {
            Field field = windManagerImplClazz.getDeclaredField("mGlobal");
            field.setAccessible(true);
            windowManagerGlobal = field.get(windowManagerImpl);
            windManagaerGlobalClass = windowManagerGlobal.getClass();
            field = windManagaerGlobalClass.getDeclaredField("mViews");
            field.setAccessible(true);
            mViews = (ArrayList<View>) field.get(windowManagerGlobal);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        View targetView = null;
        int time = 0;
        if(mViews!=null){
            for(View view:mViews){
                Log.i("LZH",time+"View: "+view.getWidth()+" "+view.getHeight()+" name: "+view.getClass().getName());
                targetView = getViewByPath(view,path);
                if(targetView!=null){
                    Log.i("LZH","find view");
                    return targetView;
                }
                time++;
            }
            return targetView;
        }
        return null;
    }
    public static View getViewByPath(View rootView,String viewPath){
        class Node{
            public String path;
            public View view;
            public Node(String path,View view){
                this.path = path;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = rootView;
        String path = decorView.getClass().getName();
        queue.add(new Node(path,decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
            if(temp.path.equals(viewPath)){
                if(temp.view instanceof TextView){
                    Log.i("LZH","text: "+((TextView) temp.view).getText());
                }
                return temp.view;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                List<View> childViews = obtainChildViews(viewGroup);
                for(int i=0;i<childViews.size();i++){
                    child = childViews.get(i);
                    queue.add(new Node(temp.path+"/"+child.getClass()+":"+i,child));
                }
            }
        }
        return null;
    }
    public static View findByViewCoordinate(View rootView,String path,float x,float y,float width,float height){
        List<String> listName = getViewNameList(path);
        View view = dfsFindView(rootView,listName,0,x,y,width,height);
        if(view!=null){
            return view;
        }
        return null;
    }
    private static View dfsFindView(View curView , List<String> viewPathNames,int pos,
                                    float x,float y,float width, float height){
        String curName = curView.getClass().getName();
        if(pos>=viewPathNames.size()){
            return null;
        }
        if(curName.equals(viewPathNames.get(pos))){
            if(pos==viewPathNames.size()-1){
                if(isSameView(curView,x,y,width,height)){
                    return curView;
                }
            }else if(curView instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup) curView;
                int childCount = viewGroup.getChildCount();
                for(int i=0;i<childCount;i++){
                    View target = dfsFindView(viewGroup.getChildAt(i),viewPathNames,pos+1,
                            x,y,width,height);
                    if(target!=null){
                        return target;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检查当前View的信息是否与给定的x,y,width,height相同
     * @param view
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    private static boolean isSameView(View view,float x,float y,float width,float height){
        if(Math.abs(obtainX(view)-x)<2&&
            Math.abs(obtainY(view)-y)<2&&
            Math.abs(obtainWidth(view)-width)<2&&
            Math.abs(obtainHeight(view)-height)<2){
            return true;
        }
        return false;
    }
    private static List<String> getViewNameList(String path){
        String viewFlag[] = path.split("/");
        List<String> listName = new ArrayList<>();
        for(String flag:viewFlag){
            String strs[] = flag.split(":");
            listName.add(strs[0]);
        }
        return listName;
    }
    public static void showView(View view){
        String str = view.getClass().getName();
        if(view instanceof TextView){
            str+=" "+((TextView) view).getText();
        }
        Log.i("LZH",""+str);
        if(view instanceof ViewGroup){
            int count = ((ViewGroup) view).getChildCount();
            for(int i=0;i<count;i++){
                showView(((ViewGroup) view).getChildAt(i));
            }

        }
    }
    /**
     * 获取activity页面中的内容
     * 返回结果中的每一个Item格式： xpath:text
     * @param activity
     * @return
     */
    public static ArrayList<String> capturePageContent(Activity activity){
        ArrayList<String> res = new ArrayList<>();
        View decorView = activity.getWindow().getDecorView();
        ViewGroup viewGroup;
        View child;
        ViewNode childNode;
        ViewNode temp;
        List<ViewNode> list = new ArrayList<>();
        list.add(new ViewNode(decorView,decorView.getClass().getName()));
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.view instanceof TextView){
                String item = temp.path+":"+((TextView) temp.view).getText();
                res.add(item);
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                List<View> childViews = obtainChildViews(viewGroup);
                for(int i=0;i<childViews.size();i++){
                    child = childViews.get(i);
                    childNode = new ViewNode(child,temp.path+"/"+child.getClass()+":"+i);
                    list.add(childNode);
                }
            }
        }
        return res;
    }
    public static List<ViewInfo> obtainStructureOfWindow(Activity activity){
        Context context = activity.getApplicationContext();
        return obtainStructureOfWindow(context);
    }
    /**
     * 生成整个页面的结构
     * @param context
     * @return
     */
    public static List<ViewInfo> obtainStructureOfWindow(Context context){
        Object windowManagerImpl = context.getSystemService(Context.WINDOW_SERVICE);
        Class windowManagerImplClass = windowManagerImpl.getClass();

        Object windowManagerGlobal = null;
        Class windowManagerGlobalClass = null;

        ArrayList<View> listViews = null;
        try {
            Field field = windowManagerImplClass.getDeclaredField("mGlobal");
            field.setAccessible(true);
            windowManagerGlobal = field.get(windowManagerImpl);
            windowManagerGlobalClass = windowManagerGlobal.getClass();
            Field viewField = windowManagerGlobalClass.getDeclaredField("mViews");
            viewField.setAccessible(true);
            listViews = (ArrayList<View>) viewField.get(windowManagerGlobal);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        List<ViewInfo> viewInfos = new ArrayList<>();
        if(listViews==null){
            return viewInfos;
        }
        for(int i=0;i<listViews.size();i++){
            View view = listViews.get(i);
            if(view==null){
                continue;
            }
            if(!isVisible(view)){
//                Log.i("LZH","Invisible: "+view.getClass().getName()+ActivityUtil.getActivity(view).getClass().getName());
                continue;
            }
            Log.i("LZH","top view: "+view.getClass().getName());
            ViewInfo viewInfo = obtainViewInfo(view,0);
            viewInfos.add(viewInfo);
        }
        return viewInfos;
    }

    /**
     * 生成某一个视图的ViewInfo
     * @param view
     * @param curIndex
     * @return
     */
    private static ViewInfo obtainViewInfo(View view,  int curIndex){
        ViewInfo viewInfo = null;
        float viewX = obtainX(view);
        float viewY = obtainY(view);
        float viewWidth = obtainWidth(view);
        float viewHeight = obtainHeight(view);
        String viewName = view.getClass().getName();
        //避免直接使用xpath
        viewInfo = new ViewInfo(viewX,viewY,viewWidth,viewHeight,
                "",viewName,curIndex);

        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            List<ViewInfo> childInfos = new ArrayList<>();
            List<View> childViews = obtainChildViews(viewGroup);
            for(int i=0;i<childViews.size();i++){
//                if(!isVisible(childViews.get(i))){
//                    continue;
//                }
                ViewInfo childViewInfo = obtainViewInfo(childViews.get(i),i);
                childInfos.add(childViewInfo);
            }
            viewInfo.setChilds(childInfos);
        }
        return viewInfo;
    }

    public static float obtainX(View view){
        int pos[] = new int[2];
        view.getLocationInWindow(pos);
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return pos[0]/density;
    }
    public static float obtainY(View view){
        int pos[] = new int[2];
        view.getLocationInWindow(pos);
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return pos[1]/density;
    }
    public static float obtainWidth(View view){
        int widthpx = view.getWidth();
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return widthpx/density;
    }
    public static float obtainHeight(View view){
        int heightpx = view.getHeight();
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return heightpx/density;
    }
    private static List<View> obtainChildViews(ViewGroup parentView){
        List<View> childs = new ArrayList<>();
        for(int i=0;i<parentView.getChildCount();i++){
            childs.add( parentView.getChildAt(i) );
        }
        Collections.sort(childs, new Comparator<View>() {
            @Override
            public int compare(View o1, View o2) {
                int res = 0;
                float val = o1.getX() - o2.getX();
                res = (int) (val/Math.abs(val));
                if(res!=0){
                    return res;
                }
                val = o1.getY() - o2.getY();
                res = (int) (val / Math.abs(val));
                if(res!=0){
                    return res;
                }
                res = o1.getWidth() - o2.getWidth();
                if(res!=0){
                    return res;
                }
                res = o1.getHeight() - o2.getHeight();
                if(res!=0){
                    return res;
                }
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });
        return childs;
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
