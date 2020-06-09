package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class DatabaseHelperInstrumentedTest {

    private Context mContext;

    private DatabaseHelper mDatabaseHelper;
    private ArrayList<String> tableNames;

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        tableNames = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tableNames.add("TABLE " + i);
        }
        mDatabaseHelper = DatabaseHelper.getInstance(mContext, tableNames);
    }

    @Test
    public void databaseCreation() {
        assertTrue(databaseExists());
    }

    @Test
    public void tableCreation() {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        String sql = "select \"name\" from \"sqlite_master\" where name like \"table\" "
//                + " name NOT LIKE 'sqlite_%' and name not like 'android_metadata%'"
                ;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        assertEquals(cursor.getCount(), tableNames.size());
    }

    private boolean databaseExists() {
        File file = mContext.getDatabasePath(DatabaseHelper.DB_NAME);
        return file.exists();
    }
}
