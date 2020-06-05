package com.sample.tanay.dynamicspinner;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class TableListUnitTest {

    @Test
    public void tableInfoCreation() {
        ArrayList<String> names = new ArrayList<>();
        names.add("1");
        names.add("2");
        names.add("3");

        TableList list = new TableList(names);

        assertEquals(list.names, names);
    }
}
