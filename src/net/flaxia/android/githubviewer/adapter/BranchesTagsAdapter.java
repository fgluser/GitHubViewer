package net.flaxia.android.githubviewer.adapter;

import net.flaxia.android.githubviewer.model.KeyValuePair;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BranchesTagsAdapter extends ArrayAdapter<KeyValuePair> {
    protected KeyValuePair[] mKeyValuePairs;
    protected LayoutInflater mInflater;
    protected int mLayout;

    public BranchesTagsAdapter(Context context, int textViewResourceId, KeyValuePair[] keyValuePairs) {
        super(context, textViewResourceId, keyValuePairs);
        mKeyValuePairs = keyValuePairs;
        mLayout = textViewResourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(mLayout, null) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(mKeyValuePairs[position]
                .getKey());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(mLayout, null) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(mKeyValuePairs[position]
                .getKey());
        return view;
    }

}
