package net.flaxia.android.githubviewer;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BookmarkDialog extends Dialog {
    public BookmarkDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_bookmark);
        String[] items = context.getResources().getStringArray(R.array.bookmark_dialog);
        ((ListView) findViewById(R.id.listView)).setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, items));
        setTitle("Menu");
    }
}
