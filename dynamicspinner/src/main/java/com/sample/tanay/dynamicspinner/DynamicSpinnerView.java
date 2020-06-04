package com.sample.tanay.dynamicspinner;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DynamicSpinnerView extends LinearLayout {

    public static void setup(Context context, String filename, SetupListener setupListener) {
        DataProcessor.newInstance(context.getApplicationContext()).setup(filename, setupListener);
    }

    private DynamicSpinnerViewListener mDynamicSpinnerViewListener;


    private SpinnerElement mSpinnerElement;

    private ArrayList<SpinnerElement> mSpinnerElements;
    private ArrayList<ViewInfo> viewInfoArrayList = new ArrayList<>();


    public DynamicSpinnerView(Context context) {
        super(context);
        setOrientationToVertical();
    }

    public DynamicSpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientationToVertical();
    }

    public DynamicSpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientationToVertical();
    }

    @TargetApi(21)
    public DynamicSpinnerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientationToVertical();
    }

    private void setOrientationToVertical() {
        setOrientation(VERTICAL);
    }


    public void setDynamicSpinnerViewListener(DynamicSpinnerViewListener mDynamicSpinnerViewListener) {
        this.mDynamicSpinnerViewListener = mDynamicSpinnerViewListener;
    }


    public void load(ArrayList<SpinnerElement> spinnerElements) {
        this.mSpinnerElements = spinnerElements;
        SpinnerThread.getInstance(getContext()).load(spinnerElements, new SpinnerThread.Listener() {
            @Override
            public void onLoadStart() {

            }

            @Override
            public void onLoadFailed(Exception exception) {

            }

            @Override
            public void onLoadSuccess(DataNode rootNode) {

                setup(rootNode);
            }
        });
    }


    private void setup(final DataNode rootNode) {
        rootNode.setAsParent(0, mSpinnerElements.size());
        final ArrayList<DataNode> leafNodes = new ArrayList<>();
        DataNode.populateLeafNodes(leafNodes, rootNode, mSpinnerElements.size(), 0);
        SearchAdapter searchAdapter = new SearchAdapter(getContext(), android.R.layout.simple_list_item_2,
                android.R.id.text1, leafNodes);
        AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(getContext());
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataNode leafNode = leafNodes.get(position);
                int index = viewInfoArrayList.size() - 1;
                while (index >= 0) {
                    viewInfoArrayList.get(index).itemToBeSelected = leafNode;
                    leafNode = leafNode.parent;
                    index--;
                }
                int sizeMinus1 = viewInfoArrayList.size() - 1;
                for (int i = 0; i < viewInfoArrayList.size(); i++) {
                    ViewInfo viewInfo = viewInfoArrayList.get(i);
                    int pos = DataNode.getPosition(viewInfo.itemToBeSelected, viewInfo.dataset);
                    viewInfo.spinner.setSelection(pos);
                    if (i < sizeMinus1) {
                        ViewInfo nextViewInfo = viewInfoArrayList.get(i + 1);
                        nextViewInfo.dataset.clear();
                        nextViewInfo.dataset.addAll(viewInfo.itemToBeSelected.children);
                        nextViewInfo.adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        autoCompleteTextView.setAdapter(searchAdapter);
        autoCompleteTextView.setThreshold(2);
        addView(autoCompleteTextView);
        int index = 0;
        for (SpinnerElement element : mSpinnerElements) {

            Spinner spinner = new Spinner(getContext());
            ArrayList<DataNode> dataset = new ArrayList<>();
            if (index == 0) {
                dataset.addAll(rootNode.children);
            }
            ArrayAdapter<DataNode> adapter = new ArrayAdapter<>(getContext(), element.resourceId, element.textViewId, dataset);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int pos = (int) parent.getTag();
                    ViewInfo viewInfo = viewInfoArrayList.get(pos);
                    DataNode selectedDataNode = viewInfo.dataset.get(position);
                    if (viewInfo.previouslySelectedDataNode != selectedDataNode) {
                        viewInfo.previouslySelectedDataNode = selectedDataNode;
                        if (pos < viewInfoArrayList.size() - 1 && selectedDataNode.children != null) {
                            ViewInfo nextViewInfo = viewInfoArrayList.get(pos + 1);
                            nextViewInfo.dataset.clear();
                            nextViewInfo.dataset.addAll(selectedDataNode.children);
                            nextViewInfo.spinner.setAdapter(null);
                            nextViewInfo.spinner.setAdapter(nextViewInfo.adapter);
                            if (nextViewInfo.itemToBeSelected != null) {
                                int positionOfNodeToBeSelected = DataNode.getPosition(nextViewInfo.itemToBeSelected, nextViewInfo.dataset);
                                if (positionOfNodeToBeSelected != -1) {
                                    nextViewInfo.spinner.setSelection(positionOfNodeToBeSelected);
                                } else {
                                    nextViewInfo.spinner.setSelection(0);
                                }
                                nextViewInfo.itemToBeSelected = null;
                            } else {
                                nextViewInfo.spinner.setSelection(0);
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    int pos = (int) parent.getTag();
                    Log.d("TAG", "nothing select " + pos);
                }
            });
            spinner.setTag(index);
            spinner.setLayoutParams(element.layoutParams);
            View view = new View(getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams
                    (PixelUtils.dpToPx(getContext(), element.separatorWidthDp),
                            PixelUtils.dpToPx(getContext(), element.separatorHeightDp)));
            addView(spinner);
            addView(view);
            ViewInfo viewInfo = new ViewInfo(dataset, adapter, spinner, index);
            viewInfoArrayList.add(viewInfo);
            index++;
            element = element.child;
        }
        if (mDynamicSpinnerViewListener != null)
            mDynamicSpinnerViewListener.onLoadComplete();
    }

    public interface DynamicSpinnerViewListener {
        void onLoadStart();

        void onLoadComplete();
    }

    public interface SetupListener {
        void onSetupStart();

        void onSetupComplete();
    }
}
