package com.sample.tanay.dynamicspinner;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

final class ViewInfo {

    ArrayList<DataNode> dataset;

    ArrayAdapter<DataNode> adapter;

    Spinner spinner;

    int level;

    DataNode previouslySelectedDataNode;

    DataNode itemToBeSelected;

    ViewInfo(ArrayList<DataNode> dataset, ArrayAdapter<DataNode> adapter, Spinner spinner,
             int level) {
        this.dataset = dataset;
        this.adapter = adapter;
        this.spinner = spinner;
        this.level = level;
    }


}
