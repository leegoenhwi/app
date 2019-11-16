package com.sunmoon_bus.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //세로로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            Thread.sleep(300);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }



}