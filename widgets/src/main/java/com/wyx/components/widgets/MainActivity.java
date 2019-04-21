package com.wyx.components.widgets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ExpandCollpaseTextView view = findViewById(R.id.text_view);
//            view.setText("上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看上课了的房价来看");
        }
    }
}
