package net.flaxia.android.githubviewer;

import java.io.File;

import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Configuration;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BlobsMenuDialog extends Dialog {
    protected LoadingDialog mLoadingDialog;
    private Refs mRefs;

    public BlobsMenuDialog(Context context, Refs refs) {
        super(context);
        mRefs = refs;
        setContentView(R.layout.dialog_menu);
        setTitle("Menu");
        initAdapter();
    }

    private void initAdapter() {
        Context context = getContext();
        String[] items = context.getResources().getStringArray(R.array.blobs_menu_dialog);
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
                        Bookmark bookmark = new Bookmark(mRefs.getOwner(), mRefs.getName(), mRefs
                                .getKey(), mRefs.getHash(), "");
                        Context context = getContext();
                        Intent intent = new Intent(context, BookmarkEditActivity.class);
                        intent.putExtra(Extra.BOOKMARK, bookmark);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1: // Bookmark now
                    boookmarkNow();
                    break;
                case 2: // Download
                    final SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getContext());
                    String targetDirPath = prefs.getString(ConfigureActivity.SAVE_DIR,
                            Configuration.DEFAULT_SAVE_PATH);
                    final File targetDir = new File(targetDirPath);
                    targetDir.mkdirs();
                    if (targetDir.canWrite()) {
                        Intent intent = new Intent(getContext(), DownloadService.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DownloadService.SAVE_PATH, targetDirPath + "/"
                                + mRefs.getOwner() + "/" + mRefs.getName() + "/" + mRefs.getKey()
                                + ".zip");
                        intent.putExtra(DownloadService.DOWNLOAD_URL, "https://github.com/"
                                + mRefs.getOwner() + "/" + mRefs.getName() + "/zipball/"
                                + mRefs.getKey());
                        getContext().startService(intent);
                    } else {
                        Toast.makeText(getContext(), R.string.could_not_write_to_external_memory,
                                Toast.LENGTH_SHORT).show();
                    }
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

    /**
     * 画面回転対策
     */
    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    /**
     * ダイアログが有効化を確認してから閉じる
     */
    protected void dismissDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
