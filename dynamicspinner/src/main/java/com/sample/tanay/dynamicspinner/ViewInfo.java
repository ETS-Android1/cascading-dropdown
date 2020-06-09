package com.sample.tanay.dynamicspinner;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

final class ViewInfo {

    ArrayList<DataNode> dataset;

    ArrayAdapter<DataNode> adapter;

    Spinner spinner;

    DataNode previouslySelectedDataNode;

    DataNode itemToBeSelected;

    ViewInfo(ArrayList<DataNode> dataset, ArrayAdapter<DataNode> adapter, Spinner spinner) {
        this.dataset = dataset;
        this.adapter = adapter;
        this.spinner = spinner;
    }

    String getSelectedInfo() {
        String result = "";
        if (spinner != null) {
            DataNode node = (DataNode) spinner.getSelectedItem();
            result = node.name;
        }
        return result;
    }

}
