package com.sample.tanay.dynamicspinnersample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;

import com.sample.tanay.dynamicspinner.DynamicSpinnerView;
import com.sample.tanay.dynamicspinner.SpinnerElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SpinnerElement> spinnerElements;
    private DynamicSpinnerView dynamicSpinnerView;

    private boolean mNeedToRegister;

    private ProgressDialog mProgressDialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case DynamicSpinnerView.SETUP_START: {
                        break;
                    }
                    case DynamicSpinnerView.SETUP_COMPLETE: {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
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

//        dynamicSpinnerView.setLazyLoadingEnabled(true);

        Set<String> set4 = new HashSet<>();
//        set4.add("State 21");
//
        spinnerElements.add(new SpinnerElement("State", set4,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), "STATE 7"));

        Set<String> set = new HashSet<>();
//        set.add("District 7");
//        set.add("District 9");


        spinnerElements.add(new SpinnerElement("District", set,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), "District 3"));


        spinnerElements.add(new SpinnerElement("Zilla",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), "Zilla 9"));

        spinnerElements.add(new SpinnerElement("Tehsil",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), "Tehsil 44"));

        spinnerElements.add(new SpinnerElement("Block",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));


        Set<String> set2 = new HashSet<>();
//        set2.add("Village 1");
//        set2.add("Village 2");

        spinnerElements.add(new SpinnerElement("Village", set2,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), null));

        Set<String> set3 = new HashSet<>();
//        set3.add("School 1");
//        set3.add("School 2");

        spinnerElements.add(new SpinnerElement("School", set3,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), null));


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
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onDatabaseNotExist() {
                mNeedToRegister = true;
                onLoadStart();
                register();
            }
        });

        dynamicSpinnerView.load(spinnerElements);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mNeedToRegister)
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
                new IntentFilter(DynamicSpinnerView.SETUP_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(DynamicSpinnerView.SETUP_START));
    }
}
