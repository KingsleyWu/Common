package com.joywe.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.joywe.common.image.ImageActivity;
import com.smart.common.http.HttpManger;
import com.smart.common.util.DebugUtil;
import com.smart.common.util.ThreadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        //startActivity(new Intent(this, ImageActivity.class));
        //DebugUtil.d(new ArrayList<>());
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwiZXhwIjo2MDAwMDE1MzUxODI5ODQsImlhdCI6MTUzNTE4MzA0NCwiYXV0aG9yaXRpZXMiOiIifQ.aZFPto4B4h2-bcugp1xH-sxG8WLRePbFXFusOkSZguY";
        HttpManger.getInstance().setToken(token);
        HttpManger.getInstance().postAsync("http://192.168.3.210:8081/auth/login", new HttpManger.ResultCallback() {
            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onResponse(String response) {
                DebugUtil.d(response);
            }
        },getLoginParams("341721"),new HttpManger.Param("Authorization",token));
    }

    private String  getLoginParams(String code){
        JSONObject params = new JSONObject();
        try {
            params.put("mobile","15089190078");
            params.put("password","");
            params.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params.toString();
    }
}
