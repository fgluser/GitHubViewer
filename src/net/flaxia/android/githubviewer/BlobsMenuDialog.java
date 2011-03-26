package net.flaxia.android.githubviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Configuration;
import net.flaxia.android.githubviewer.util.Downloader;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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
        setContentView(R.layout.dialog_blobs_menu);
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
                    downloadAndUnZip();
                    break;
                }
                BlobsMenuDialog.this.dismiss();
            }
        });
    }

    private void downloadAndUnZip() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog = new LoadingDialog(getOwnerActivity(), R.string.downloading);
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final File targetDir = new File(prefs.getString(ConfigureActivity.SAVE_DIR,
                Configuration.DEFAULT_SAVE_PATH)
                + "/" + mRefs.getOwner() + "/" + mRefs.getName());
        targetDir.mkdirs();
        try {
            final String targetPath = targetDir.getAbsolutePath();
            final URL url = new URL("https://github.com/" + mRefs.getOwner() + "/"
                    + mRefs.getName() + "/zipball/" + mRefs.getKey());
            new Thread(new Runnable() {
                public void run() {
                    Downloader.start(url, new File(targetDir + "/" + mRefs.getKey() + ".zip"));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.dismiss();
                            mLoadingDialog = new LoadingDialog(getOwnerActivity(),
                                    R.string.unzipping);
                        }
                    });
                    unzip(targetPath);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }).start();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            mLoadingDialog.dismiss();
            Toast.makeText(getContext(), R.string.could_not_find_file_to_download,
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void unzip(final String outPath) {
        File outDir = new File(outPath + "/" + mRefs.getKey());
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try {
            ZipFile zipFile = new ZipFile(outPath + "/" + mRefs.getKey() + ".zip");
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File outFile = new File(outDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdir();
                } else {
                    bin = new BufferedInputStream(zipFile.getInputStream(entry));
                    bout = new BufferedOutputStream(new FileOutputStream(outFile));
                    int bytedata = 0;
                    while ((bytedata = bin.read()) != -1) {
                        bout.write(bytedata);
                    }
                    bout.flush();
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bin != null)
                    bin.close();
                if (bout != null)
                    bout.close();
            } catch (IOException e) {
            }
        }
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
