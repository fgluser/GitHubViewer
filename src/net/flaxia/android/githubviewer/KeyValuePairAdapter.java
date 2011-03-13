package net.flaxia.android.githubviewer;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KeyValuePairAdapter extends ArrayAdapter<KeyValuePair> {
    private ArrayList<KeyValuePair> mKeyValuePairs;
    private LayoutInflater mInflater;
    private int mLayout;

    public KeyValuePairAdapter(Context context, int textViewResourceId,
            ArrayList<KeyValuePair> keyValuePairs) {
        super(context, textViewResourceId, keyValuePairs);
        mLayout = textViewResourceId;
        mKeyValuePairs = keyValuePairs;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public String getValue(int index) {
        return mKeyValuePairs.get(index).getValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(mLayout, null) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(mKeyValuePairs.get(position)
                .getKey());
        return view;
    }
}
