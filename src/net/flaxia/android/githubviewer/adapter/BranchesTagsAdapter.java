package net.flaxia.android.githubviewer.adapter;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.model.KeyValuePair;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BranchesTagsAdapter extends ArrayAdapter<KeyValuePair> {
    protected LayoutInflater mInflater;
    protected int mLayout;

    public BranchesTagsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId, new ArrayList<KeyValuePair>());
        mLayout = textViewResourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(mLayout, null) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getKey());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getKey());
        return view;
    }

}
