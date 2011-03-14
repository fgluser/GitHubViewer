package net.flaxia.android.githubviewer;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.IconCache;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TreeAdapter extends KeyValuePairAdapter {

    public TreeAdapter(Context context, int textViewResourceId,
            ArrayList<KeyValuePair> keyValuePairs) {
        super(context, textViewResourceId, keyValuePairs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(mLayout, null) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(mKeyValuePairs.get(position)
                .getKey());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        ((TextView) view.findViewById(android.R.id.text1)).setText(Html.fromHtml(CommonHelper
                .multiply("<img src=\"dir_blank\">", Integer.parseInt(mKeyValuePairs.get(position)
                        .getValue()))
                + mKeyValuePairs.get(position).getKey(), IconCache.getInstance(), null));
        return view;
    }
}
