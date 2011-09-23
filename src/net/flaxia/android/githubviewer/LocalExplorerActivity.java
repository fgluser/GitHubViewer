
package net.flaxia.android.githubviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import net.flaxia.android.githubviewer.adapter.ExplorerAdapter;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

public class LocalExplorerActivity extends ListActivity {
    public void onResume() {
        super.onResume();
        final String path = getIntent().getExtras().getString(Extra.EXPLORER_PATH);
        setTitle(path);
        final File[] files = getSortedFiles(path);
        final String[] list = new String[files.length];
        int count = 0;
        String name = "";

        for (final File file : files) {
            if (file.isDirectory()) {
                name = file.getName() + "/";
            } else {
                name = file.getName();
            }
            list[count] = name;
            count++;
        }
        setListAdapter(new ExplorerAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, list));

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final File file = files[position];
                final Intent intent = new Intent();
                intent.putExtra(Extra.EXPLORER_PATH, file.getAbsolutePath());
                if (file.isDirectory()) {
                    intent.setClass(getApplicationContext(), LocalExplorerActivity.class);
                } else {
                    intent.setClass(getApplicationContext(), CodeViewActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    private File[] getSortedFiles(final String path) {
        final File[] source = new File(path).listFiles();
        final ArrayList<File> dirList = new ArrayList<File>();
        final ArrayList<File> fileList = new ArrayList<File>();

        for (final File file : source) {
            if (file.isDirectory()) {
                dirList.add(file);
            } else {
                fileList.add(file);
            }
        }
        final File[] dirs = dirList.toArray(new File[0]);
        final File[] files = fileList.toArray(new File[0]);

        Arrays.sort(dirs, new FileSort());
        Arrays.sort(files, new FileSort());

        final File[] sorted = new File[dirs.length + files.length];
        System.arraycopy(dirs, 0, sorted, 0, dirs.length);
        System.arraycopy(files, 0, sorted, dirs.length, files.length);

        return sorted;
    }

    static class FileSort implements Comparator<File> {
        public int compare(File file1, File file2) {
            return file1.getName().compareTo(file2.getName());
        }
    }
}
