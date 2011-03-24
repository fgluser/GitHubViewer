package net.flaxia.android.githubviewer;

import java.util.ArrayList;

import net.flaxia.android.githubviewer.adapter.BookmarkAdapter;
import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import android.app.ListActivity;
import android.os.Bundle;

public class BookmarkActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new BookmarkAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_2, new ArrayList<Bookmark>()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BookmarkSQliteOpenHelper bookmarkDb = new BookmarkSQliteOpenHelper(getApplicationContext());
        Bookmark[] bookmarks = bookmarkDb.select();
        for (Bookmark bookmark : bookmarks) {
            ((BookmarkAdapter) getListAdapter()).add(bookmark);
        }
    }
}
