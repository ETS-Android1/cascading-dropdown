package com.sample.tanay.dynamicspinner;

import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.Set;

public final class SpinnerElement {


    static boolean hasValues(ArrayList<SpinnerElement> spinnerElements) {
        boolean flag = false;
        for (SpinnerElement spinnerElement : spinnerElements) {
            if (spinnerElement.hasValues()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    static ArrayList<SpinnerElement> getSubset(int startIndex, ArrayList<SpinnerElement> spinnerElements) {
        ArrayList<SpinnerElement> list = new ArrayList<>();
        for (++startIndex; startIndex < spinnerElements.size(); startIndex++) {
            list.add(spinnerElements.get(startIndex));
        }
        return list;
    }

    String type;
    Set<String> values;

    ViewGroup.LayoutParams layoutParams;

    @LayoutRes
    int resourceId = android.R.layout.simple_dropdown_item_1line;

    @IdRes
    int textViewId = android.R.id.text1;

    @LayoutRes
    int dropdownResId = -1;

    int separatorWidthDp = 0, separatorHeightDp = 8;

    public SpinnerElement(String type, ViewGroup.LayoutParams layoutParams) {
        this(type, null, layoutParams);
    }

    public SpinnerElement(String type, Set<String> values, ViewGroup.LayoutParams layoutParams) {
        this.type = type;
        this.values = values;
        this.layoutParams = layoutParams;
    }


    boolean hasValues() {
        return values != null && values.size() > 0;
    }
}
