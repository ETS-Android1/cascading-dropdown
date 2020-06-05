package com.sample.tanay.dynamicspinner;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class ViewInfoUnitTest {

    @Test
    public void assignment() {
        Spinner spinner = mock(Spinner.class);
        ArrayList<DataNode> list = new ArrayList<>();
        ArrayAdapter<DataNode> adapter = mock(ArrayAdapter.class);

        ViewInfo viewInfo = new ViewInfo(list, adapter, spinner);
        assertEquals(viewInfo.adapter, adapter);
        assertEquals(viewInfo.dataset, list);
        assertEquals(viewInfo.spinner, spinner);
    }
}
