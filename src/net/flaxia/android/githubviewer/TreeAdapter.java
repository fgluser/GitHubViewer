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
        int level = Integer.parseInt(mKeyValuePairs.get(position).getValue());
        if (level == 0) {
            ((TextView) view.findViewById(android.R.id.text1)).setText(mKeyValuePairs.get(position)
                    .getKey());
        } else {
            String html = "";
            boolean flag = false;
            int i = position + 1;
            while (i < mKeyValuePairs.size()) {
                int value = Integer.parseInt(mKeyValuePairs.get(i).getValue());
                if (value == level) {
                    html = "<img src=\"dir_t\">";
                    flag = true;
                    break;
                }
                if (value < level) {
                    break;
                }
                i++;
            }
            if (!flag) {
                html = "<img src=\"dir_l\">";
            }
            int downLevel = level - 1;
            while (0 != downLevel) {
                int k = position + 1;
                while (k < mKeyValuePairs.size()) {
                    int value = Integer.parseInt(mKeyValuePairs.get(k).getValue());
                    if (value == downLevel) {
                        html = "<img src=\"dir_i\">" + html;
                        break;
                    }
                    if (value < downLevel) {
                        html = "<img src=\"dir_blank\">" + html;
                        break;
                    }
                    k++;
                }
                downLevel--;
            }
            ((TextView) view.findViewById(android.R.id.text1)).setText(Html.fromHtml(html
                    + mKeyValuePairs.get(position).getKey(), IconCache.getInstance(), null));
        }
        return view;
    }
}
