package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class SharedPrefHelperUnitTest {

    private SharedPrefHelper mSharedPrefHelper;
    private SharedPreferences mSharedPreference;

    @Before
    public void setup() {
        Context context = mock(Context.class);
        mSharedPreference = mock(SharedPreferences.class);
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getPackageName()).thenReturn("abc");
        when(context.getSharedPreferences(
                "abc_pref", Context.MODE_PRIVATE))
                .thenReturn(mSharedPreference);


        mSharedPrefHelper = SharedPrefHelper.helper(context);
    }

    @Test
    public void databaseStatus() {
        when(mSharedPreference.getBoolean(SharedPrefHelper.DB_SAVED, false)).thenReturn(false);
        assertFalse(mSharedPrefHelper.isDbSaved());
        when(mSharedPreference.getBoolean(SharedPrefHelper.DB_SAVED, false)).thenReturn(true);
        assertTrue(mSharedPrefHelper.isDbSaved());
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(mSharedPreference.edit()).thenReturn(editor);
        when(editor.putBoolean(SharedPrefHelper.DB_SAVED, true)).thenReturn(editor);
        mSharedPrefHelper.setDbSaved();
        verify(editor).putBoolean(SharedPrefHelper.DB_SAVED, true);
        verify(editor).apply();
    }

    @Test
    public void tables() {
        ArrayList<String> names = new ArrayList<>();
        for (int index = 0; index < 5; index++)
            names.add("NAME " + index);
        TableList list = new TableList(names);
        String json = new Gson().toJson(list);
        when(mSharedPreference.getString(SharedPrefHelper.TABLE_LIST,
                "{\"names\":\"[]\"}")).thenReturn(json);
        ArrayList<String> copy = mSharedPrefHelper.getTableList();

        assertEquals(copy.size(), names.size());

        for (int i = 0; i < names.size(); i++) {
            assertEquals(names.get(i), copy.get(i));
        }

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(mSharedPreference.edit()).thenReturn(editor);
        when(editor.putString(SharedPrefHelper.TABLE_LIST, json)).thenReturn(editor);
        mSharedPrefHelper.saveTableList(list);
        verify(editor).putString(SharedPrefHelper.TABLE_LIST, json);
        verify(editor).apply();
    }
}
