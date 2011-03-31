package net.flaxia.android.githubviewer.adapter;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.IconCache;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KeyValuePairAdapter extends BaseListAdapter<KeyValuePair> {
    public KeyValuePairAdapter(Context context, int textViewResourceId,
            ArrayList<KeyValuePair> keyValuePairs) {
        super(context, textViewResourceId, keyValuePairs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        String fileName = getItem(position).getKey();
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(fileName);

        Drawable drawable = IconCache.getInstance().getDrawable(
                "type_" + CommonHelper.getSuffix(fileName));
        if (null == drawable) {
            drawable = IconCache.getInstance().getDrawable("type_unknown");
        }
        textView.setCompoundDrawables(drawable, null, null, null);

        return view;
    }
}
