package com.sample.tanay.dynamicspinner;

import android.view.ViewGroup;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SpinnerElementUnitTest {

    @Test
    public void spinnerElementValueAssignment() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        String type = "abc";
        Set<String> set = new HashSet<>();
        SpinnerElement element = new SpinnerElement(type, set, params, null);
        assertEquals(element.type, type);
        assertEquals(element.layoutParams, params);
        assertEquals(element.values, set);
    }

    @Test
    public void spinnerElementHasValues() {
        SpinnerElement element1 = new SpinnerElement("abc", null);
        assertFalse(element1.hasValues());
        HashSet<String> sets = new HashSet<>();
        SpinnerElement element2 = new SpinnerElement("abc", sets, null, null);
        assertFalse(element2.hasValues());
        sets.add("abc");
        assertTrue(element2.hasValues());
    }


    @Test
    public void spinnerElementListHasValues() {
        SpinnerElement element1 = new SpinnerElement("abc", null);
        HashSet<String> sets = new HashSet<>();
        SpinnerElement element2 = new SpinnerElement("abc", sets, null, null);
        ArrayList<SpinnerElement> elements = new ArrayList<>();
        elements.add(element1);
        elements.add(element2);
        assertFalse(SpinnerElement.hasValues(elements));
        HashSet<String> sets2 = new HashSet<>();
        SpinnerElement element3 = new SpinnerElement("abc", sets2, null, null);
        sets2.add("A");
        elements.add(element3);
        assertTrue(SpinnerElement.hasValues(elements));
    }

    @Test
    public void spinnerElementListSubset() {
        SpinnerElement element1 = new SpinnerElement("abc", null);
        SpinnerElement element2 = new SpinnerElement("abc1", null);
        SpinnerElement element3 = new SpinnerElement("abc2", null);
        ArrayList<SpinnerElement> spinnerElements = new ArrayList<>();
        spinnerElements.add(element1);
        spinnerElements.add(element2);
        spinnerElements.add(element3);
        ArrayList<SpinnerElement> subset = SpinnerElement.getSubset(1, spinnerElements);
        assertEquals(subset.size(), 1);
        assertEquals(subset.get(0), element3);
    }

    @Test
    public void resourceIdAndTextId() {
        int resId = 11, textId = 12;
        SpinnerElement element1 = new SpinnerElement("abc", null);
        element1.setResourceAndTextId(resId, textId);
        assertEquals(resId, element1.resourceId);
        assertEquals(textId, element1.textViewId);
    }

    @Test
    public void dropdownId() {
        int id = 1;
        SpinnerElement element1 = new SpinnerElement("abc", null);
        element1.setDropdownResId(id);
        assertEquals(element1.dropdownResId, id);
    }
}