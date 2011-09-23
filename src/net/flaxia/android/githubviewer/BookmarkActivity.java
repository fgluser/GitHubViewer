
package net.flaxia.android.githubviewer;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.adapter.BookmarkAdapter;
import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class BookmarkActivity extends ListActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new BookmarkAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_2, new ArrayList<Bookmark>()));
        initListenner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final BookmarkSQliteOpenHelper bookmarkDb = new BookmarkSQliteOpenHelper(
                getApplicationContext());
        final BookmarkAdapter adapter = (BookmarkAdapter) getListAdapter();
        final Bookmark[] bookmarks = bookmarkDb.select();
        for (Bookmark bookmark : bookmarks) {
            adapter.add(bookmark);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((BookmarkAdapter) getListAdapter()).clear();
    }

    private void initListenner() {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Bookmark bookmark = (Bookmark) ((ListView) parent)
                        .getItemAtPosition(position);
                final Refs refs = new Refs(bookmark.getOwner(), bookmark.getName(), bookmark
                        .getTree(),
                        bookmark.getHash());
                final Intent intent = new Intent(getApplicationContext(), BlobsActivity.class);
                intent.putExtra(Extra.REFS, refs);
                startActivity(intent);
                finish();
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Bookmark bookmark = (Bookmark) ((ListView) parent)
                        .getItemAtPosition(position);
                new BookmarkMenuDialog(BookmarkActivity.this, bookmark).show();
                return false;
            }
        });
    }
}
