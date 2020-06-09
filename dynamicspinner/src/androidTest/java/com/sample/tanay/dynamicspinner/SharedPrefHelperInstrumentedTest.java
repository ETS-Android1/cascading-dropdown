package com.sample.tanay.dynamicspinner;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class SharedPrefHelperInstrumentedTest {

    private SharedPrefHelper mSharedPrefHelper;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mSharedPrefHelper = SharedPrefHelper.helper(context);

    }

    @Test
    public void instance() {
        assertNotNull(mSharedPrefHelper);
    }

    @Test
    public void databaseStatus() {
        assertFalse(mSharedPrefHelper.isDbSaved());
        mSharedPrefHelper.setDbSaved(true);
        assertTrue(mSharedPrefHelper.isDbSaved());
    }

    @Test
    public void tableInfo() {
        ArrayList<String> names = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            names.add("NAME " + index);
        }
        assertEquals(mSharedPrefHelper.getTableList().size(), 0);
        mSharedPrefHelper.saveTableList(new TableList(names));
        ArrayList<String> retreivedList = mSharedPrefHelper.getTableList();
        assertEquals(retreivedList.size(), names.size());
        for (int i = 0; i < names.size(); i++) {
            assertEquals(names.get(i), retreivedList.get(i));
        }
    }

    @Test
    public void dbVersion() {
        int dbVersion1 = 5, dbVersion2 = 6;
        mSharedPrefHelper.setDatabaseVersion(dbVersion1);
        assertEquals(dbVersion1, mSharedPrefHelper.getDatabaseVersion());
        mSharedPrefHelper.setDatabaseVersion(dbVersion2);
        assertEquals(dbVersion2, mSharedPrefHelper.getDatabaseVersion());
    }
}
