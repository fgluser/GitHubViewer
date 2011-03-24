package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditBookmarkActivity extends Activity {
    public static final int ADD = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bookmark);

        Refs refs = (Refs) getIntent().getExtras().getSerializable(Extra.REFS);
        ((TextView) findViewById(R.id.owner)).setText(refs.getOwner());
        ((TextView) findViewById(R.id.name)).setText(refs.getName());
        ((TextView) findViewById(R.id.tree)).setText(refs.getKey());
    }

    public void onOkButton(View view) {
        int id = getIntent().getExtras().getInt(BookmarkSQliteOpenHelper.COLUMN_ID);
        BookmarkSQliteOpenHelper bookmark = new BookmarkSQliteOpenHelper(getApplicationContext());
        Refs refs = (Refs) getIntent().getExtras().getSerializable(Extra.REFS);
        boolean flag = true;
        if (ADD == id) {
            long result = bookmark.insert(refs.getOwner(), refs.getName(), refs.getKey(), refs
                    .getHash(), ((EditText) findViewById(R.id.note)).getText().toString());
            if(BookmarkSQliteOpenHelper.FAIL == result){
                flag = false;
            }
        } else {
            int result = bookmark.update(id, refs.getOwner(), refs.getName(), refs.getKey(), refs
                    .getHash(), ((EditText) findViewById(R.id.note)).getText().toString());
            if(0 == result){
                flag = false;
            }
        }
        if(flag){
            Toast.makeText(getApplicationContext(), R.string.succeed_to_edit_a_bookmark, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), R.string.failed_to_edit_a_bookmark, Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }

    public void onCancelButton(View view) {
        finish();
    }
}
