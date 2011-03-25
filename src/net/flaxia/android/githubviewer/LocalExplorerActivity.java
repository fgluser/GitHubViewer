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
        String path = getIntent().getExtras().getString(Extra.EXPLORER_PATH);
        setTitle(path);
        final File[] files = getSortedFiles(path);
        String[] list = new String[files.length];
        int count = 0;
        String name = "";

        for (File file : files) {
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = files[position];
                Intent intent = new Intent();
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

    private File[] getSortedFiles(String path) {
        File[] source = new File(path).listFiles();
        ArrayList<File> dirList = new ArrayList<File>();
        ArrayList<File> fileList = new ArrayList<File>();

        for (File file : source) {
            if (file.isDirectory()) {
                dirList.add(file);
            } else {
                fileList.add(file);
            }
        }
        File[] dirs = dirList.toArray(new File[0]);
        File[] files = fileList.toArray(new File[0]);

        Arrays.sort(dirs, new FileSort());
        Arrays.sort(files, new FileSort());

        File[] sorted = new File[dirs.length + files.length];
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
