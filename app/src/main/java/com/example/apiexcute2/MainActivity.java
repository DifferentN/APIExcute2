package com.example.apiexcute2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.apiexcute2.ViewManager.FloatViewManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        FloatViewManager floatViewManager = FloatViewManager.getInstance(this);
        floatViewManager.showSaveIntentViewBt();

    }
}
