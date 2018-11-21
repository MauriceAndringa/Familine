package com.example.familine;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startMain();
            }
        }, 750);
    }

    public void startMain(){
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }
}
