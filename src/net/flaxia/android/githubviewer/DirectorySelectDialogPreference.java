
package net.flaxia.android.githubviewer;

import java.io.File;

import net.flaxia.android.githubviewer.DirectorySelectDialog.onDirectoryListDialogListener;
import net.flaxia.android.githubviewer.util.Configuration;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class DirectorySelectDialogPreference extends DialogPreference implements
        DirectorySelectDialog.onDirectoryListDialogListener {

    public DirectorySelectDialogPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(final View view) {
        final SharedPreferences pref = getSharedPreferences();
        String summry = Configuration.DEFAULT_SAVE_PATH;
        if (null != pref) {
            summry = pref.getString(getKey(), summry);
        }
        setSummary(summry);
        super.onBindView(view);
    }

    @Override
    protected void onClick() {
        final File externalStorage = Environment.getExternalStorageDirectory();
        final DirectorySelectDialog dlg = new DirectorySelectDialog(getContext());
        dlg.setOnFileListDialogListener((onDirectoryListDialogListener) this);
        dlg.show(externalStorage.getAbsolutePath(), externalStorage.getPath());
    }

    @Override
    public void onClickFileList(final String path) {
        if (null != path) {
            final SharedPreferences.Editor editor = getEditor();
            editor.putString(getKey(), path);
            editor.commit();
            notifyChanged();
        }
    }
}
