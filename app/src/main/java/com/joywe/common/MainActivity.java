package com.joywe.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.joywe.common.image.ImageActivity;
import com.smart.common.util.DebugUtil;
import com.smart.common.util.ThreadUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        startActivity(new Intent(this, ImageActivity.class));
        //DebugUtil.d(new ArrayList<>());
    }
}
