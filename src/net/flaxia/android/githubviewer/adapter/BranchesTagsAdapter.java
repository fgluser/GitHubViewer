
package net.flaxia.android.githubviewer.adapter;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.model.KeyValuePair;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BranchesTagsAdapter extends BaseListAdapter<KeyValuePair> {
    public BranchesTagsAdapter(final Context context, final int textViewResourceId) {
        super(context, textViewResourceId, new ArrayList<KeyValuePair>());
        setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
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
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getKey());

        return view;
    }

}
