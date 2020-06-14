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
        DynamicSpinnerView.setup(this /* application context required to read from assets*/,
                "sample.json" /* full name of the JSON file in the assets folder*/,
                new DynamicSpinnerView.SetupListener() {
                    @Override
                    public void onSetupComplete() {

                    }

                    @Override
                    public void onSetupProcessStart() {

                    }
                } /* an instance of the listener which will be called during the setup process*/,
                2 /*the version code in integer, if you want to use a different file as data
                 data source from the old file then the version code needs to be incremented*/);

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
