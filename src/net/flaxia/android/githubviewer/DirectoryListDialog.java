package net.flaxia.android.githubviewer;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DirectoryListDialog extends Activity implements DialogInterface.OnClickListener {
    private Context mContext;
    private ArrayList<File> mDirectoryList;
    private onDirectoryListDialogListener mListenner;

    public DirectoryListDialog(Context context) {
        mContext = context;
        mDirectoryList = new ArrayList<File>();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if ((null != mDirectoryList) && (null != mListenner)) {
            File file = mDirectoryList.get(which);
            show(file.getAbsolutePath(), file.getPath());
        }
    }

    public void show(final String path, String title) {
        try {
            File[] mDirectories = new File(path).listFiles();
            if (null == mDirectories && null != mListenner) {
                mListenner.onClickFileList(null);
            } else {
                mDirectoryList.clear();
                ArrayList<String> viewList = new ArrayList<String>();
                for (File file : mDirectories) {
                    if (file.isDirectory()) {
                        viewList.add(file.getName() + "/");
                        mDirectoryList.add(file);
                    }
                }

                // ダイアログ表示
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(title);
                alertDialogBuilder.setItems(viewList.toArray(new String[0]), this);
                // 自身のContextではgetStringが失敗する
                alertDialogBuilder.setPositiveButton(mContext.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println(path);
                                mListenner.onClickFileList(path);
                            }
                        });
                // 自身のContextではgetStringが失敗する
                alertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListenner.onClickFileList(null);
                            }
                        });
                alertDialogBuilder.show();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void setOnFileListDialogListener(onDirectoryListDialogListener listener) {
        mListenner = listener;
    }

    public interface onDirectoryListDialogListener {
        public void onClickFileList(String path);
    }
}
