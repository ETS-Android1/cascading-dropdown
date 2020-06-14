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

    /**
     * Represents the type of entity. Must correspond
     * to a key in the source JSON file.
     */
    String type;

    /**
     * If this is not empty then only those values will be fetched
     * whose name is exists in the set.
     */

    Set<String> values;

    /**
     * The {@link android.view.ViewGroup.LayoutParams} instance used to set the
     * layout params of the spinner.
     */

    ViewGroup.LayoutParams layoutParams;

    @LayoutRes
    int resourceId = android.R.layout.simple_dropdown_item_1line;

    @IdRes
    int textViewId = android.R.id.text1;

    @LayoutRes
    int dropdownResId = -1;

    /**
     * The height and width of the separators between any two spinners.
     * There is no separator above the first spinner.
     */

    int separatorWidthDp = 0, separatorHeightDp = 8;

    String valueToBeSelected;

    public SpinnerElement(String type, ViewGroup.LayoutParams layoutParams) {
        this(type, null, layoutParams, null);
    }

    public SpinnerElement(String type, ViewGroup.LayoutParams layoutParams, String valueToBeSelected) {
        this(type, null, layoutParams, valueToBeSelected);
    }

    public SpinnerElement(String type, Set<String> values, ViewGroup.LayoutParams layoutParams,
                          String valueToBeSelected) {
        this.type = type;
        this.values = values;
        this.layoutParams = layoutParams;
        this.valueToBeSelected = valueToBeSelected;
    }


    boolean hasValues() {
        return values != null && values.size() > 0;
    }

    public void setResourceAndTextId(@LayoutRes int layoutId, @IdRes int textViewId) {
        this.resourceId = layoutId;
        this.textViewId = textViewId;
    }

    public void setDropdownResId(@LayoutRes int layoutId) {
        this.dropdownResId = layoutId;
    }
}
