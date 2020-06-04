package com.sample.tanay.dynamicspinner;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DynamicSpinnerService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public DynamicSpinnerService() {
        super("DynamicSpinnerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }



}
