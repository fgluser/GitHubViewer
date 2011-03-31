package net.flaxia.android.githubviewer.adapter;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.model.Bookmark;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookmarkAdapter extends BaseListAdapter<Bookmark> {
    public BookmarkAdapter(Context context, int textViewResourceId, ArrayList<Bookmark> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Bookmark bookmark = getItem(position);
        ((TextView) view.findViewById(android.R.id.text1)).setText(bookmark.getName() + " / "
                + bookmark.getOwner());
        ((TextView) view.findViewById(android.R.id.text2)).setText(bookmark.getNote());

        return view;
    }
}
