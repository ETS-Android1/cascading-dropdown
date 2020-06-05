package com.sample.tanay.dynamicspinnersample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sample.tanay.dynamicspinner.DynamicSpinnerView;
import com.sample.tanay.dynamicspinner.SpinnerElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SpinnerElement> spinnerElements;
    private DynamicSpinnerView dynamicSpinnerView;

    private ProgressDialog mProgressDialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "org.samagra.SETUP_START": {
                        Log.d("time", "step 1 setup start " + System.currentTimeMillis());
                        break;
                    }
                    case "org.samagra.SETUP_COMPLETE": {
                        dynamicSpinnerView.load(spinnerElements);
                        break;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dynamicSpinnerView = findViewById(R.id.dynamicSpinnerView);
        spinnerElements = new ArrayList<>();

        register();

        Set<String> set4 = new HashSet<>();
//        set4.add("State 21");
//
        spinnerElements.add(new SpinnerElement("State", set4,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        Set<String> set = new HashSet<>();
//        set.add("District 7");
//        set.add("District 9");


        spinnerElements.add(new SpinnerElement("District", set,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));


        spinnerElements.add(new SpinnerElement("Zilla",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        spinnerElements.add(new SpinnerElement("Tehsil",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        spinnerElements.add(new SpinnerElement("Block",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));


        Set<String> set2 = new HashSet<>();
//        set2.add("Village 1");
//        set2.add("Village 2");

        spinnerElements.add(new SpinnerElement("Village", set2,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        Set<String> set3 = new HashSet<>();
//        set3.add("School 1");
//        set3.add("School 2");

        spinnerElements.add(new SpinnerElement("School", set3,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));


        dynamicSpinnerView.setLazyLoadingEnabled(true);
        dynamicSpinnerView.setDynamicSpinnerViewListener(new DynamicSpinnerView.DynamicSpinnerViewListener() {
            @Override
            public void onLoadStart() {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading", null);
                }
                mProgressDialog.show();
            }

            @Override
            public void onLoadComplete() {
                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
            }
        });

        DynamicSpinnerView.setup(this, "demo10.json",
                new DynamicSpinnerView.SetupListener() {
                    @Override
                    public void onSetupStart() {
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading", null);
                        }
                        mProgressDialog.show();
                    }

                    @Override
                    public void onSetupComplete() {
                        if (mProgressDialog != null) {
                            mProgressDialog.hide();
                        }
                        dynamicSpinnerView.load(spinnerElements);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void register() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("org.samagra.SETUP_COMPLETE"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("org.samagra.SETUP_START"));
    }
}
