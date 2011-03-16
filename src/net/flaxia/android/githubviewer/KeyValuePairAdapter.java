package net.flaxia.android.githubviewer;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.IconCache;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KeyValuePairAdapter extends ArrayAdapter<KeyValuePair> {
    protected ArrayList<KeyValuePair> mKeyValuePairs;
    protected LayoutInflater mInflater;
    protected int mLayout;

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
        String fileName = mKeyValuePairs.get(position).getKey();
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(fileName);

        Drawable drawable = IconCache.getInstance().getDrawable(
                "type_" + CommonHelper.getSuffix(fileName));
        if(null == drawable){
            drawable = IconCache.getInstance().getDrawable("type_unknown");
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        
        return view;
    }
}
