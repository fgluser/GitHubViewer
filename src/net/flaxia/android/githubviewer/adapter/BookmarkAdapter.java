
package net.flaxia.android.githubviewer.adapter;

import java.util.List;

import net.flaxia.android.githubviewer.model.Bookmark;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookmarkAdapter extends BaseListAdapter<Bookmark> {
    public BookmarkAdapter(final Context context, final int textViewResourceId,
            final List<Bookmark> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        final Bookmark bookmark = getItem(position);
        ((TextView) view.findViewById(android.R.id.text1)).setText(bookmark.getName() + " / "
                + bookmark.getOwner());
        ((TextView) view.findViewById(android.R.id.text2)).setText(bookmark.getNote());

        return view;
    }
}
