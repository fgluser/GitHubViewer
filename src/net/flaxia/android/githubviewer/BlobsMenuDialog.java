package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BlobsMenuDialog extends Dialog {
    Refs mRefs;

    public BlobsMenuDialog(Context context, Refs refs) {
        super(context);
        mRefs = refs;
        setContentView(R.layout.dialog_bookmark_menu);
        setTitle("Menu");
        initAdapter();
    }

    private void initAdapter() {
        Context context = getContext();
        String[] items = context.getResources().getStringArray(R.array.bookmark_menu_dialog);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0: // Add Bookmark
                    try {
                        Context context = getContext();
                        Intent intent = new Intent(context, EditBookmarkActivity.class);
                        intent.putExtra(BookmarkSQliteOpenHelper.COLUMN_ID,
                                EditBookmarkActivity.ADD);
                        intent.putExtra(Extra.REFS, mRefs);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1: // Bookmark now
                    boookmarkNow();
                    break;
                }
                BlobsMenuDialog.this.dismiss();
            }
        });
    }

    private void boookmarkNow() {
        BookmarkSQliteOpenHelper bookmark = new BookmarkSQliteOpenHelper(getContext());
        long result = bookmark.insert(mRefs.getOwner(), mRefs.getName(), mRefs.getKey(), mRefs
                .getHash(), CommonHelper.getNow());

        if (BookmarkSQliteOpenHelper.FAIL == result) {
            Toast.makeText(getContext(), R.string.failed_to_edit_a_bookmark, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getContext(), R.string.added_to_your_bookmarks, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
