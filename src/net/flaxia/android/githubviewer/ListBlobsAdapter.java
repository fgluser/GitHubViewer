package net.flaxia.android.githubviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListBlobsAdapter extends ArrayAdapter<KeyValuePair> {
    private KeyValuePair[] mKeyValuePairs;
    private LayoutInflater mInflater;

    public ListBlobsAdapter(Context context, int textViewResourceId, KeyValuePair[] keyValuePairs) {
        super(context, textViewResourceId, keyValuePairs);
        mKeyValuePairs = keyValuePairs;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public String getValue(int index){
        return mKeyValuePairs[index].getValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(android.R.layout.simple_list_item_1,
                null) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(mKeyValuePairs[position]
                .getKey());
        return view;
    }
}
