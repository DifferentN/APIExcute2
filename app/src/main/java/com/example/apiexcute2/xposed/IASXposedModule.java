package com.example.apiexcute2.xposed;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.example.apiexcute2.xposed.event.ActivityOnCreateHook;
import com.example.apiexcute2.xposed.event.ActivityOnResumeHook;
import com.example.apiexcute2.xposed.event.HookOnDraw;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class IASXposedModule implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        hookMethod(ValueAnimator.class,"endAnimation");
//        XposedHelpers.findAndHookMethod("android.support.v4.widget.DrawerLayout",lpparam.classLoader,"computeScroll",new ViewComputeScroll());
//        XposedHelpers.findAndHookMethod("android.widget.OverScroller",lpparam.classLoader,"computeScrollOffset",new ViewStartAniamtion());
//        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onDraw",Canvas.class,new ViewOnDraw());
        Log.i("LZH","Loaded app: "+lpparam.packageName);
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new ActivityOnCreateHook(lpparam));
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new ActivityOnResumeHook());
        if(lpparam.packageName.equals("com.starbucks.cn")){
            //com.starbucks.cn com.ichi2.anki com.douban.movie
            initHook(lpparam);
        }
    }
    private void initHook(XC_LoadPackage.LoadPackageParam lpparam){
        XposedHelpers.findAndHookMethod("android.animation.ValueAnimator",lpparam.classLoader,"addUpdateListener",
                ValueAnimator.AnimatorUpdateListener.class,new ValueAnimatorAddUpdateListener());
        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"postInvalidateDelayed",long.class,new ViewInvalidate());
        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"invalidateInternal",int.class,int.class,int.class,int.class,
                boolean.class,boolean.class,new ViewInvalidate());

        //类名视具体情况而定
        //android.support.v4.widget.DrawerLayout
        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"computeScroll",new ViewComputeScroll());
        XposedHelpers.findAndHookMethod("android.widget.OverScroller",lpparam.classLoader,"computeScrollOffset",new ScrollComputeScrollOffset());
        XposedHelpers.findAndHookMethod("android.widget.Scroller",lpparam.classLoader,"computeScrollOffset",new ScrollComputeScrollOffset());

        //同时监听ViewPropertyAnimator动画是否完成
        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"animate",new ViewAnimate());
        XposedHelpers.findAndHookMethod("android.view.ViewPropertyAnimator",lpparam.classLoader,"setListener", Animator.AnimatorListener.class,new ViewPropertySetAnimatorListener());

        //监听valueAnimator/ObjectAnimator是否完成
        //注意版本不同,mumu为6.0版本，endAnimation需要参数
        hookMethod(ValueAnimator.class,"endAnimation");
        //监听view的补间动画是否完成
        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"applyLegacyAnimation",ViewGroup.class,long.class,Animation.class,boolean.class,new ViewApplyLegacyAnimation());
        //监听view的滑动是否完成
        XposedHelpers.findAndHookMethod("android.widget.Scroller",lpparam.classLoader,"computeScrollOffset",new HookScrollerOverScroller());
        XposedHelpers.findAndHookMethod("android.widget.OverScroller",lpparam.classLoader,"computeScrollOffset",new HookScrollerOverScroller());

        XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());
        Log.i("LZH","hook init end");
    }

    private void hookMethod(Class clazz,String methodName){
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method:methods){
            int methodId = method.getModifiers();
            if(Modifier.isAbstract(methodId)||Modifier.isInterface(methodId)||Modifier.isNative(methodId)){
                continue;
            }
            if(method.getName().equals(methodName)){
                XposedBridge.hookMethod(method,new ValueAnimatorEndAnimation());
            }
        }
    }
}
