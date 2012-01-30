
package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BookmarkMenuDialog extends Dialog {
    private final Bookmark mBookmark;
    private final HomeActivity mHomeActivity;

    public BookmarkMenuDialog(final HomeActivity bookmarkActivity, Bookmark bookmark) {
        super(bookmarkActivity);
        mHomeActivity = bookmarkActivity;
        mBookmark = bookmark;
        setContentView(R.layout.dialog_menu);
        setTitle("Menu");
        initAdapter();
    }

    private void initAdapter() {
        final Context context = getContext();
        final String[] items = context.getResources().getStringArray(R.array.bookmark_menu_dialog);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, items);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Context context = getContext();
                final Intent intent = new Intent();
                switch (position) {
                    case 0: // open
                        intent.setClass(context, BlobsActivity.class);
                        final Refs refs = new Refs(mBookmark.getOwner(), mBookmark.getName(),
                                mBookmark
                                        .getTree(), mBookmark.getHash());
                        intent.putExtra(Extra.REFS, refs);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case 1: // edit
                        intent.setClass(context, BookmarkEditActivity.class);
                        intent.putExtra(Extra.BOOKMARK, mBookmark);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case 2: // remove
                        final BookmarkSQliteOpenHelper db = new BookmarkSQliteOpenHelper(context);
                        db.delete(mBookmark.getId());
                        mHomeActivity.reloadBookmark();
                        break;
                }
                BookmarkMenuDialog.this.dismiss();
            }
        });
    }
}
