package com.example.mauriceandringa.familine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                startMain();
            }
        }, 750);





    }
    public void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    };
}
