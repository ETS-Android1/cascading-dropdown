package com.sample.tanay.dynamicspinner;

import android.util.SparseIntArray;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataNodeUnitTest {

    private static final String NODE = "NODE";

    private static final String[] CHILDREN = {"child 1", "child 2", "child 3", "child 4"};

    @Test
    public void dataNodetoString() {
        DataNode dataNode = new DataNode(NODE);
        assertEquals(dataNode.toString(), NODE);
    }

    @Test
    public void getChildViaId() {
        DataNode dataNode = new DataNode(NODE);
        dataNode.children = new ArrayList<>();
        for (int i = 0; i < CHILDREN.length; i++) {
            dataNode.children.add(new DataNode(CHILDREN[i], i));
        }

        for (int i = CHILDREN.length - 1; i >= 0; i--) {
            DataNode child = dataNode.getChild(i);
            assertNotNull(child);
            assertEquals((int) child.id, i);
            assertEquals(child.name, CHILDREN[i]);
        }
    }

    @Test
    public void getChildViaName() {
        DataNode dataNode = new DataNode(NODE);
        dataNode.children = new ArrayList<>();
        for (int i = 0; i < CHILDREN.length; i++) {
            dataNode.children.add(new DataNode(CHILDREN[i], i));
        }

        for (int i = CHILDREN.length - 1; i >= 0; i--) {
            DataNode child = dataNode.getChild(CHILDREN[i]);
            assertNotNull(child);
            assertEquals((int) child.id, i);
            assertEquals(child.name, CHILDREN[i]);
        }
    }

    @Test
    public void assignParent() {
        DataNode dataNode = new DataNode(NODE);
        dataNode.children = new ArrayList<>();

        dataNode.children = new ArrayList<>();
        for (int i = 0; i < CHILDREN.length; i++) {
            dataNode.children.add(new DataNode(CHILDREN[i], i));
        }

        dataNode.setAsParent(0, 1);

        for (DataNode child : dataNode.children) {
            assertEquals(child.parent, dataNode);
        }
    }

    @Test
    public void assignParentId() {
        DataNode dataNode = new DataNode(NODE);
        dataNode.children = new ArrayList<>();

        dataNode.children = new ArrayList<>();
        for (int i = 0; i < CHILDREN.length; i++) {
            dataNode.children.add(new DataNode(CHILDREN[i], i));
        }

        dataNode.id = (int) (Math.random() * 100);
        dataNode.assignParentId(false);

        for (DataNode child : dataNode.children) {
            assertNull(child.parentId);
        }

        dataNode.assignParentId(true);

        for (DataNode child : dataNode.children) {
            assertEquals(child.parentId, dataNode.id);
        }
    }

    @Test
    public void assignId() {
        DataNode dataNode = new DataNode(NODE);
        dataNode.children = new ArrayList<>();

        dataNode.children = new ArrayList<>();
        dataNode.children.add(new DataNode(CHILDREN[0], 1));


        SparseIntArray sparseIntArray = mock(SparseIntArray.class);
        when(sparseIntArray.get(0)).thenReturn(0);
        when(sparseIntArray.get(1)).thenReturn(0);

        dataNode.assignId(0, sparseIntArray);

        assertEquals((int) dataNode.id, 1);

        int index = 1;

        for (DataNode child : dataNode.children) {
            assertEquals((int) child.id, index);
            index++;
        }
    }
}