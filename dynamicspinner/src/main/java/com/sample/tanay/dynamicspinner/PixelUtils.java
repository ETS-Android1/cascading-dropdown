package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.util.TypedValue;

final class PixelUtils {

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
}
