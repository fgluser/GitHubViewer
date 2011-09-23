
package net.flaxia.android.githubviewer.adapter;

import net.flaxia.android.githubviewer.model.Repositorie;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RepositorieAdapter extends BaseListAdapter<Repositorie> {

    public RepositorieAdapter(final Context context, final int textViewResourceId,
            final Repositorie[] repositories) {
        super(context, textViewResourceId, repositories);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        final Repositorie repositorie = getItem(position);
        ((TextView) view.findViewById(android.R.id.text1)).setText(repositorie.get("name"));
        ((TextView) view.findViewById(android.R.id.text2)).setText(repositorie.get("description"));

        return view;
    }
}
