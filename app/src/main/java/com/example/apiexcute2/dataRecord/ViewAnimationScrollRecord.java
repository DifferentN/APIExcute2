package com.example.apiexcute2.dataRecord;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ViewAnimationScrollRecord {
    private static ViewAnimationScrollRecord record;
    //记录<ValueAnimator,View>，其中view的动画由valueAnimator实现
    private List<RecordItem> valueAnimatorRecords;
    //记录<ViewPropertyAnimator,View>
    private List<RecordItem> viewPropertyRecords;
    //记录<Scroll/OverScroll，View>
    private List<RecordItem> scrollRecords;
    public ViewAnimationScrollRecord(){
        valueAnimatorRecords = new ArrayList<>();
        viewPropertyRecords = new ArrayList<>();
        scrollRecords = new ArrayList<>();
    }
    public static ViewAnimationScrollRecord getInstance(){
        if(record==null){
            synchronized (ViewAnimationScrollRecord.class){
                if(record==null){
                    record = new ViewAnimationScrollRecord();
                }
            }
        }
        return record;
    }

    /**
     * 获取通过valueAnimator实现动画的view
     * @param valueAnimator
     * @return
     */
    public List<View> getViewByValueAnimator(ValueAnimator valueAnimator){
        List<View> res = new ArrayList<>();
        for(int i=valueAnimatorRecords.size()-1;i>=0;i--){
            RecordItem recordItem = valueAnimatorRecords.get(i);
            Object animator = recordItem.getMoveObj();
            if(animator==null){
                valueAnimatorRecords.remove(i);
            }if(recordItem.getMoveObj()==valueAnimator){
                res.add(recordItem.getView());
            }
        }
        return res;
    }
    public View getViewByViewPropertyAnimator(ViewPropertyAnimator viewPropertyAnimator){
        return getViewFromRecords(viewPropertyAnimator,viewPropertyRecords);
    }
    public View getViewByScrollOrOverScroll(Object targetScroll){
        return  getViewFromRecords(targetScroll,scrollRecords);
    }
    private View getViewFromRecords(Object targetMoveObj,List<RecordItem> list){
        int size = list.size();
        for(int i=size-1;i>=0;i--){
            RecordItem recordItem = list.get(i);
            //得到ValueAnimator,scroll,OverScroll对象
            Object moveObject = recordItem.getMoveObj();
            if(moveObject==null){
                list.remove(i);
            }else if(moveObject==targetMoveObj){
                return recordItem.getView();
            }
        }
        return null;
    }

    /**
     * 记录ValueAnimator和通过ValueAnimator实现动画的View
     * @param valueAnimator
     * @param view
     */
    public void addValueAnimatorRecord(ValueAnimator valueAnimator,View view){
        addAnimatorScrollAndView(valueAnimator,view,valueAnimatorRecords);
    }
    public void addViewPropertyAnimatorRecord(ViewPropertyAnimator viewPropertyAnimator
            ,View view){
        addAnimatorScrollAndView(viewPropertyAnimator,view,viewPropertyRecords);
    }
    public void addScrollRecord(Object obj,View view){
        addAnimatorScrollAndView(obj,view,scrollRecords);
    }
    private void addAnimatorScrollAndView(Object obj,View view,List<RecordItem> list){
        RecordItem recordItem = new RecordItem(view,obj);
        list.add(recordItem);
    }

    /**
     * valueAnimator结束时，将其从记录中移除
     * @param valueAnimator
     */
    public void removeValueAnimatorRecord(ValueAnimator valueAnimator){
        removeAnimatorOrScroll(valueAnimator,valueAnimatorRecords);
    }
    public void removeViewPropertyAnimatorRecord(ViewPropertyAnimator viewPropertyAnimator){
        removeAnimatorOrScroll(viewPropertyAnimator,viewPropertyRecords);
    }
    public void removeScrollRecord(Object targetObj){
        removeAnimatorOrScroll(targetObj,scrollRecords);
    }
    private void removeAnimatorOrScroll(Object targetObj,List<RecordItem> list){
        for(int i=list.size()-1;i>=0;i--){
            RecordItem recordItem = list.get(i);
            Object recordObj = recordItem.getMoveObj();
            if(recordObj==null){
                list.remove(i);
            }else if(recordObj==targetObj){
                list.remove(i);
            }
        }
    }

    /**
     * 查看是否已包含<valueAnimator,view>记录
     * valueAnimator与view可能存在多对多的关系
     * @param valueAnimator
     * @param view
     * @return
     */
    public boolean containValueAnimatorView(ValueAnimator valueAnimator,View view){
        for(int i=valueAnimatorRecords.size()-1;i>=0;i--){
            RecordItem recordItem = valueAnimatorRecords.get(i);
            Object animator = recordItem.getMoveObj();
            View recordView = recordItem.getView();
            if(animator==null||recordView==null){
                valueAnimatorRecords.remove(i);
            }else if(animator==valueAnimator&&view==recordView){
                return true;
            }
        }
        return false;
    }
    public boolean containScrollView(Object scrollObject,View view){
        for(int i=scrollRecords.size()-1;i>=0;i--){
            RecordItem recordItem = scrollRecords.get(i);
            Object recordScroll = recordItem.getMoveObj();
            View recordView = recordItem.getView();
            if(recordScroll==null||recordView==null){
                scrollRecords.remove(i);
            }else if(recordScroll==scrollObject&&view==recordView){
                return true;
            }
        }
        return false;
    }
    private static class RecordItem{
        //保存受动画或滑动影响的view
        private WeakReference<View> viewRecord;
        //保存scroll,OverScroll,ValueAnimator对象
        private WeakReference<Object> animateScrollRecord;
        public RecordItem(View view, Object obj){
            viewRecord = new WeakReference<>(view);
            animateScrollRecord = new WeakReference<>(obj);
        }
        public View getView(){
            return viewRecord.get();
        }
        public Object getMoveObj(){
            return animateScrollRecord.get();
        }
    }
}
