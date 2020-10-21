package com.example.apiexcute2.ViewManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.apiexcute2.receive.LocalActivityReceiver;
import com.example.apiexcute2.view.FloatButtonView;

import static com.example.apiexcute2.serve.ServeReceiver.OPEN_ACTIVITY_NAME;
import static com.example.apiexcute2.serve.ServeReceiver.OPEN_API;
import static com.example.apiexcute2.serve.ServeReceiver.OPEN_API_NAME;
import static com.example.apiexcute2.serve.ServeReceiver.OPEN_PACKAGE_NAME;


public class FloatViewManager {
    private FloatButtonView saveIntentView;
    private FloatButtonView openAPIView;
    private static FloatViewManager floatViewManager;
    private WindowManager.LayoutParams layoutParams;
    private Context context;
    private Activity activity;
    private WindowManager windowManager;
    public FloatViewManager(Context context){
        this.context = context;
        this.activity = (Activity) context;
    }
    public static FloatViewManager getInstance(Context context){
        if(floatViewManager == null){
            floatViewManager = new FloatViewManager(context);
        }
        return floatViewManager;
    }

    public void showSaveIntentViewBt(){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        saveIntentView = new FloatButtonView(context);
        if(layoutParams == null){
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = saveIntentView.width;
            layoutParams.height = saveIntentView.height;
            layoutParams.gravity = Gravity.BOTTOM|Gravity.LEFT;
            if (Build.VERSION.SDK_INT > 24) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.format = PixelFormat.RGBA_8888;

            layoutParams.x = 0;
            layoutParams.y = 0;
        }

        saveIntentView.setLayoutParams(layoutParams);

        windowManager.addView(saveIntentView,layoutParams);

        saveIntentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LZH","click createBt");
                Intent intent = new Intent();
                intent.setAction(LocalActivityReceiver.START_EVENT);
                context.sendBroadcast(intent);
            }
        });
    }
    public void showOPenAPIViewBt(){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        openAPIView = new FloatButtonView(context);
        if(true){
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = openAPIView.width;
            layoutParams.height = openAPIView.height;
            layoutParams.gravity = Gravity.BOTTOM|Gravity.RIGHT;
            if (Build.VERSION.SDK_INT > 24) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.format = PixelFormat.RGBA_8888;

            layoutParams.x = 0;
            layoutParams.y = 0;
        }

        openAPIView.setLayoutParams(layoutParams);

        windowManager.addView(openAPIView,layoutParams);

        openAPIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LZH","click createBt");
                Intent intent = new Intent();
                intent.setAction(OPEN_API);
                // api 文件路径
                intent.putExtra(OPEN_API_NAME,"/storage/emulated/0/ankiLogDetail.txt");
                // 首页ActivityName
                intent.putExtra(OPEN_ACTIVITY_NAME,"com.ichi2.anki.DeckPicker");
                intent.putExtra(OPEN_PACKAGE_NAME,"com.ichi2.anki");
                context.sendBroadcast(intent);
            }
        });
    }
}
