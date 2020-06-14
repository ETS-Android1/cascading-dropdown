package com.sample.tanay.dynamicspinnersample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sample.tanay.dynamicspinner.DynamicSpinnerView;

public class SplashActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DynamicSpinnerView.setup(this, "sample.json", new DynamicSpinnerView.SetupListener() {
            @Override
            public void onSetupComplete() {

            }

            @Override
            public void onSetupProcessStart() {

            }
        }, 2);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}
