
package net.flaxia.android.githubviewer.adapter;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.util.IconCache;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TreeAdapter extends BaseListAdapter<KeyValuePair> {
    public TreeAdapter(final Context context, final int textViewResourceId,
            final ArrayList<KeyValuePair> keyValuePairs) {
        super(context, textViewResourceId, keyValuePairs);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getKey());

        return view;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        final View view = super.getDropDownView(position, convertView, parent);
        final int level = Integer.parseInt(getItem(position).getValue());
        final StringBuilder sb = new StringBuilder();

        for (int i = 1; i < level; i++) {
            boolean flag = true;
            for (int j = position + 1, size = getCount(); j < size; j++) {
                final int value = Integer.parseInt(getItem(j).getValue());
                if (value == i) {
                    sb.append("<img src=\"dir_i\">");
                    flag = false;
                    break;
                }
                if (value < i) {
                    sb.append("<img src=\"dir_blank\">");
                    flag = false;
                    break;
                }
            }
            if (flag) {
                sb.append("<img src=\"dir_blank\">");
            }
        }

        boolean flag = true;
        for (int i = position + 1, size = getCount(); i < size; i++) {
            final int value = Integer.parseInt(getItem(i).getValue());
            if (value == level) {
                sb.append("<img src=\"dir_t\">");
                flag = false;
                break;
            } else if (value < level) {
                break;
            }
        }
        if (flag && 0 != level) {
            sb.append("<img src=\"dir_l\">");
        }

        sb.append("<img src=\"dir\"> ");
        ((TextView) view.findViewById(android.R.id.text1)).setText(Html.fromHtml(sb.toString()
                + getItem(position).getKey(), IconCache.getInstance(), null));

        return view;
    }
}
