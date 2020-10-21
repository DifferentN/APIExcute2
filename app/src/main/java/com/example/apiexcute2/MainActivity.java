package com.example.apiexcute2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apiexcute2.ViewManager.FloatViewManager;
import com.example.apiexcute2.serve.MyServe;
import com.example.apiexcute2.serve.ServeReceiver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import static com.example.apiexcute2.receive.LocalActivityReceiver.ON_RESUME;
import static com.example.apiexcute2.serve.ServeReceiver.API_RESPONSE;
import static com.example.apiexcute2.serve.ServeReceiver.OPEN_API;

public class MainActivity extends AppCompatActivity {
    private int time;
    private String FLAG = "flag";
    private View view = null;
    private View view2;
    private MyTextView myTextView;
    private MyServe serve;
    private ServeReceiver serveReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        view = findViewById(R.id.linearLayout);
        view2 = findViewById(R.id.textView);
        myTextView = findViewById(R.id.myTextView);
//        view.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

        serve = new MyServe(8888);
        serve.setActivityWeakReference(this);
        serveReceiver = new ServeReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ON_RESUME);
        intentFilter.addAction(API_RESPONSE);
        intentFilter.addAction(OPEN_API);
        registerReceiver(serveReceiver,intentFilter);
        serve.setServeReceiverWeakReference(serveReceiver);
        serveReceiver.bindServe(serve);
        try {
            serve.start();
            Log.i("LZH","start serve");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(){
        FloatViewManager floatViewManager = FloatViewManager.getInstance(this);
//        floatViewManager.showSaveIntentViewBt();
//        floatViewManager.showOPenAPIViewBt();
    }
    public void click(View v){
        Log.i("LZH","view mLeft "+view.getLeft()+" layerType: "+view.getLayerType());
        Log.i("LZH","view2 mLeft "+view2.getTop()+" x "+view2.getY()+" tranlationX: "+view2.getTranslationX());
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
//        view.scrollTo(10,10);
//        myTextView.setText("123");
//        ((TextView)view2).setText("123");

    }
    public void click2(View view){
        Log.i("LZH","click2");
    }
    private void test(){
        Class viewClass = View.class;
        try {
            Field attachField = viewClass.getDeclaredField("mAttachInfo");
            attachField.setAccessible(true);
            Object attachObject = attachField.get(view);
            if(attachObject==null){
                Log.i("LZH","attach is null");
            }else{
                Log.i("LZH","attach is not  null");
            }
            Class attachClass = attachObject.getClass();
            Field isAccelerate = attachClass.getDeclaredField("mHardwareAccelerated");
            isAccelerate.setAccessible(true);
            boolean  isAccObject = (boolean) isAccelerate.get(attachObject);
            Log.i("LZH",""+isAccObject);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serveReceiver);
    }
}
