package net.flaxia.android.githubviewer.adapter;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.IconCache;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExplorerAdapter extends BaseListAdapter<String> {
    public ExplorerAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        String fileName = getItem(position);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(" " + fileName);
        Drawable drawable;
        if (fileName.contains("/")) {
            drawable = IconCache.getInstance().getDrawable("ex_dir");
        } else {
            drawable = IconCache.getInstance().getDrawable(
                    "type_" + CommonHelper.getSuffix(fileName));
        }
        if (null == drawable) {
            drawable = IconCache.getInstance().getDrawable("type_unknown");
        }
        textView.setCompoundDrawables(drawable, null, null, null);

        return view;
    }
}
