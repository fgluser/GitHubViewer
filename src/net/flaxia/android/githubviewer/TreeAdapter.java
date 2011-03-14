package net.flaxia.android.githubviewer;

import java.util.ArrayList;

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
        String html = "";
        boolean flag = true;
        for (int i = position + 1, size = mKeyValuePairs.size(); i < size; i++) {
            int value = Integer.parseInt(mKeyValuePairs.get(i).getValue());
            if (value == level) {
                html = "<img src=\"dir_t\">";
                flag = false;
                break;
            } else if (value < level) {
                break;
            }
        }
        if (flag && 0 != level) {
            html = "<img src=\"dir_l\">";
        }

        for (int i = level - 1; 0 < i; i--) {
            for (int j = position + 1, size = mKeyValuePairs.size(); j < size; j++) {
                int value = Integer.parseInt(mKeyValuePairs.get(j).getValue());
                if (value == i) {
                    html = "<img src=\"dir_i\">" + html;
                    break;
                }
                if (value < i) {
                    html = "<img src=\"dir_blank\">" + html;
                    break;
                }
            }
        }
        ((TextView) view.findViewById(android.R.id.text1)).setText(Html.fromHtml(html
                + mKeyValuePairs.get(position).getKey(), IconCache.getInstance(), null));

        return view;
    }
}