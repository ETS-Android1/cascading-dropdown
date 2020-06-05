package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)

public class PixelUtilsUnitTest {

    /**
     * Need to set the value in build.gradle based on device screen density.
     */
    @Test
    public void dpToPx() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int px = PixelUtils.dpToPx(context, 12);
        assertEquals(px, BuildConfig.PIXEL_VALUE_OF_12);
    }
}