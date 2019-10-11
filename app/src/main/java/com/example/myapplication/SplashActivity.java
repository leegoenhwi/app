package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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