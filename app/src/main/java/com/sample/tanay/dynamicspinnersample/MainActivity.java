package com.sample.tanay.dynamicspinnersample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

        spinnerElements.add(new SpinnerElement("State",
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        Set<String> set = new HashSet<>();
        set.add("District 1");
        set.add("District 3");


        spinnerElements.add(new SpinnerElement("District", null, set,
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
        set2.add("Village 1");
        set2.add("Village 2");

        spinnerElements.add(new SpinnerElement("Village", null, set2,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        Set<String> set3 = new HashSet<>();
        set3.add("School 1");
        set3.add("School 2");

        spinnerElements.add(new SpinnerElement("School", null, set3,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)));

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate();
            }
        });
        DynamicSpinnerView.setup(this, "sample.json", new DynamicSpinnerView.SetupListener() {
            @Override
            public void onSetupStart() {

            }

            @Override
            public void onSetupComplete() {
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

    private void generate() {
        JSONArray jsonArray = new JSONArray();

        for (int i1 = 1; i1 < 11; i1++) {
            String state = "STATE " + i1;
            for (int i2 = 1; i2 < 6; i2++) {
                String district = "District " + i2;
                for (int i3 = 1; i3 < 4; i3++) {
                    String zilla = "Zilla " + i3;
                    for (int i4 = 1; i4 < 6; i4++) {
                        String tehsil = "Tehsil " + i4;
                        for (int i5 = 1; i5 < 6; i5++) {
                            String block = "Block " + i5;
                            for (int i6 = 1; i6 < 6; i6++) {
                                String village = "Village " + i6;
                                for (int i7 = 1; i7 <= 7; i7++) {
                                    String school = "School " + i7;
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("State", state);
                                        jsonObject.put("District", district);
                                        jsonObject.put("Zilla", zilla);
                                        jsonObject.put("Tehsil", tehsil);
                                        jsonObject.put("Block", block);
                                        jsonObject.put("Village", village);
                                        jsonObject.put("School", school);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    jsonArray.put(jsonObject);
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d("json is ", jsonArray.toString());
    }
}
