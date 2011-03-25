package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BookmarkEditActivity extends Activity {
    public static final long ADD = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bookmark);

        Bookmark bookmark = (Bookmark) getIntent().getExtras().getSerializable(Extra.BOOKMARK);
        ((TextView) findViewById(R.id.owner)).setText(bookmark.getOwner());
        ((TextView) findViewById(R.id.name)).setText(bookmark.getName());
        ((TextView) findViewById(R.id.tree)).setText(bookmark.getTree());
        ((EditText) findViewById(R.id.note)).setText(bookmark.getNote());
    }

    public void onOkButton(View view) {
        Bookmark bookmark = (Bookmark) getIntent().getExtras().getSerializable(Extra.BOOKMARK);
        long id = bookmark.getId();
        BookmarkSQliteOpenHelper db = new BookmarkSQliteOpenHelper(getApplicationContext());
        boolean flag = true;
        if (ADD == id) {
            long result = db.insert(bookmark.getOwner(), bookmark.getName(), bookmark.getTree(),
                    bookmark.getHash(), ((EditText) findViewById(R.id.note)).getText().toString());
            if (BookmarkSQliteOpenHelper.FAIL == result) {
                flag = false;
            }
        } else {
            int result = db.update(id, bookmark.getOwner(), bookmark.getName(), bookmark.getTree(),
                    bookmark.getHash(), ((EditText) findViewById(R.id.note)).getText().toString());
            if (0 == result) {
                flag = false;
            }
        }
        if (flag) {
            Toast.makeText(getApplicationContext(), R.string.succeed_to_edit_a_bookmark,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.failed_to_edit_a_bookmark,
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    public void onCancelButton(View view) {
        finish();
    }
}
