package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

class SharedPrefHelper {

    private static SharedPrefHelper instance;
    private static final String DB_SAVED = "dbSaved";
    private static final String TABLE_LIST = "tableList";
    private static final String DB_VERSION = "dbVersion";

    synchronized static SharedPrefHelper helper(Context context) {
        if (instance == null) {
            instance = new SharedPrefHelper(context.getApplicationContext());
        }
        return instance;
    }

    private SharedPreferences sharedPreferences;

    private SharedPrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName()
                + "_pref", Context.MODE_PRIVATE);
    }

    boolean isDbSaved() {
        return sharedPreferences.getBoolean(DB_SAVED, false);
    }

    void setDbSaved(boolean value) {
        sharedPreferences.edit().putBoolean(DB_SAVED, value).apply();
    }

    void saveTableList(TableList tableList) {
        String json = new Gson().toJson(tableList);
        sharedPreferences.edit().putString(TABLE_LIST, json).apply();
    }

    ArrayList<String> getTableList() {
        String json = sharedPreferences.getString(TABLE_LIST, "{\"names\":[]}");
        return new Gson().fromJson(json, TableList.class).names;
    }

    void setDatabaseVersion(int value) {
        sharedPreferences.edit().putInt(DB_VERSION, value).apply();
    }

    int getDatabaseVersion() {
        return sharedPreferences.getInt(DB_VERSION, -1);
    }

    boolean isNewVersion(int versionCode) {
        return versionCode > getDatabaseVersion();
    }

}
