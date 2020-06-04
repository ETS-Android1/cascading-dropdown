package com.sample.tanay.dynamicspinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class SearchAdapter extends ArrayAdapter<DataNode> {

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null && constraint.length() > 0) {
                ArrayList<DataNode> suggestions = new ArrayList<>();
                for (DataNode dataNode : mItems) {
                    String distanceString = (dataNode.name.length() > constraint.length()) ?
                            dataNode.name.substring(0, constraint.length()) :
                            dataNode.name.toLowerCase();
                    if (dataNode.name.toLowerCase().startsWith(constraint.toString().toLowerCase())
                            || distance(distanceString, constraint.toString().toLowerCase()) <= 2) {
                        suggestions.add(dataNode);
                    }
                }
                FilterResults results = new FilterResults();
                results.count = suggestions.size();
                results.values = suggestions;
                return results;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<DataNode> dataNodes = (ArrayList<DataNode>) results.values;
            clear();
            if (results.count > 0) {
                addAll(dataNodes);
            }
            notifyDataSetChanged();
        }
    };

    private ArrayList<DataNode> mItems;
    private LayoutInflater mLayoutInflater;
    private int layoutId, textResId;

    public SearchAdapter(Context context, @LayoutRes int layoutId,
                         @IdRes int textResId, ArrayList<DataNode> items) {
        super(context, layoutId, textResId, items);
        this.mItems = new ArrayList<>(items);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.textResId = textResId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mLayoutInflater.inflate(layoutId, parent, false);
        } else {
            view = convertView;
        }
        DataNode dataNode = getItem(position);
        TextView textView = view.findViewById(textResId);
        textView.setText(dataNode.getSuggestionText());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
