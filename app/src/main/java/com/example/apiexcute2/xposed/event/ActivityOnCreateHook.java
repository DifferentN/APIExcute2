package com.example.apiexcute2.xposed.event;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.example.apiexcute2.receive.LocalActivityReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by vector on 16/8/4.
 * 只用来配合工具，主要是用来查看页面结构和打印intent序列
 */
public class ActivityOnCreateHook extends XC_MethodHook {

    XC_LoadPackage.LoadPackageParam loadPackageParam;
    
    public ActivityOnCreateHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);

        final Context context = (Context) param.thisObject;

        Activity activity = (Activity) param.thisObject;
        ComponentName componentName = activity.getComponentName();
        injectReceiver(context, activity);

        Log.i("LZH","after create "+componentName.getClassName());
    }




    private void injectReceiver(Context context, Activity activity) {
        //注册一个广播接收器，可以用来接收指令，这里是用来回去指定view的xpath路径的

        LocalActivityReceiver receiver = new LocalActivityReceiver(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocalActivityReceiver.START_EVENT);
        filter.addAction(LocalActivityReceiver.EXECUTE_EVENT);
        filter.addAction(LocalActivityReceiver.ON_RESUME);
        filter.addAction(LocalActivityReceiver.CAPTURE_PAGE_CONTENT);

        Object o = XposedHelpers.getAdditionalInstanceField(activity,"iasReceiver");
        if(o!=null){
            return;
        }
//        Log.i("LZH","register boradeciver "+activity.getClass().getName());
        XposedHelpers.setAdditionalInstanceField(activity, "iasReceiver", receiver);
//        activity.getApplication().registerReceiver(receiver,filter);
        activity.registerReceiver(receiver,filter);
    }
    private void showClassName(String pkName, Context context){
        List<String> names = getClassName(pkName,context);
        for(String name:names){
            Log.i("LZH_ClassName",name);
        }
    }
    public List<String > getClassName(String packageName, Context context){
        List<String > classNameList=new ArrayList<String >();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = (String) enumeration.nextElement();
                classNameList.add(className);
//                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
//                    classNameList.add(className);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }
    private void writeAnkiClassName(String pkName, Context context,String fileName){
        List<String> names = getClassName(pkName,context);
        File file = new File(fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //清空文件
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //添加类名
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for(String name:names){
                writer.write(name+"\n");
            }
            writer.flush();
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
