
package net.flaxia.android.githubviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.flaxia.android.githubviewer.adapter.BookmarkAdapter;
import net.flaxia.android.githubviewer.adapter.ExplorerAdapter;
import net.flaxia.android.githubviewer.adapter.RepositorieAdapter;
import net.flaxia.android.githubviewer.model.Bookmark;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Repositorie;
import net.flaxia.android.githubviewer.util.BookmarkSQliteOpenHelper;
import net.flaxia.android.githubviewer.util.Configuration;
import net.flaxia.android.githubviewer.util.Extra;

import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.Repository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.TabPageIndicator;

public class HomeActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<RepositorieAdapter> {
    private View mSearchView;
    private ListView mSearchResultListView;
    private ListView mBookmarkListView;
    private TextView mFilePathTextView;
    private ListView mLocalListView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final LayoutInflater layoutInflater = getLayoutInflater();
        mSearchView = layoutInflater.inflate(R.layout.activity_search, null);
        mSearchResultListView = (ListView) mSearchView.findViewById(R.id.repositorie);
        mBookmarkListView = new ListView(getBaseContext());
        final View localView = layoutInflater.inflate(R.layout.local, null);
        mFilePathTextView = (TextView) localView.findViewById(R.id.file_path);
        mLocalListView = (ListView) localView.findViewById(R.id.file_list);
        final View[] views = new View[] {
                mSearchView, mBookmarkListView, localView,
        };
        final String[] titles = new String[] {
                "Search", "Bookmark", "Local"
        };

        initSearchResultListView();
        initBookmarkListView();
        initLocalListListener();
        initLocal();

        final HomePagerAdapter adapter = new HomePagerAdapter(titles, Arrays.asList(views));
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
    }

    private String initFilePath() {
        if (0 == mFilePathTextView.getText().length()) {
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            final File targetDir = new File(prefs.getString(ConfigureActivity.SAVE_DIR,
                    Configuration.DEFAULT_SAVE_PATH));
            targetDir.mkdirs();
            mFilePathTextView.setText(targetDir.getPath());
        }
        return mFilePathTextView.getText().toString();
    }

    private void initLocalListListener() {
        mLocalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final ListAdapter listAdapter = mLocalListView.getAdapter();
                final String select = (String) listAdapter.getItem(position);
                if (select.equals("..")) {
                    final File file = new File(mFilePathTextView.getText().toString());
                    mFilePathTextView.setText(file.getParent());
                    initLocal();
                } else {
                    final File file = new File(mFilePathTextView.getText().toString() + "/"
                            + select);
                    if (file.isDirectory()) {
                        if (file.canRead()) {
                            mFilePathTextView.setText(file.getAbsolutePath());
                            initLocal();
                        } else {
                            Toast.makeText(getBaseContext(),
                                    getString(R.string.could_not_read_to_external_storage),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        final Intent intent = new Intent(getBaseContext(), CodeViewActivity.class);
                        intent.putExtra(Extra.EXPLORER_PATH, file.getAbsolutePath());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initLocal() {
        final String filePath = initFilePath();
        final File[] files = getSortedFiles(filePath);
        final List<String> list = new ArrayList<String>();
        if (!filePath.equals("/")) {
            list.add("..");
        }
        for (final File file : files) {
            list.add(file.isDirectory() ? file.getName() + "/" : file.getName());
        }
        mLocalListView.setAdapter(new ExplorerAdapter(getBaseContext(),
                android.R.layout.simple_list_item_1, list));
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

    public void initBookmark() {
        final BookmarkSQliteOpenHelper bookmarkDb = new BookmarkSQliteOpenHelper(getBaseContext());
        final List<Bookmark> bookmarks = Arrays.asList(bookmarkDb.select());
        final BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(getBaseContext(),
                android.R.layout.simple_list_item_2, bookmarks);
        mBookmarkListView.setAdapter(bookmarkAdapter);
    }

    private void initBookmarkListView() {
        initBookmark();
        mBookmarkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Bookmark bookmark = (Bookmark) ((ListView) parent)
                        .getItemAtPosition(position);
                final Refs refs = new Refs(bookmark.getOwner(), bookmark.getName(), bookmark
                        .getTree(), bookmark.getHash());
                final Intent intent = new Intent(getApplicationContext(), BlobsActivity.class);
                intent.putExtra(Extra.REFS, refs);
                startActivity(intent);
                finish();
            }
        });
        mBookmarkListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Bookmark bookmark = (Bookmark) ((ListView) parent)
                        .getItemAtPosition(position);
                new BookmarkMenuDialog(HomeActivity.this, bookmark).show();
                return false;
            }
        });
    }

    /**
     * 検索ボタンが押されたときの処理
     * 
     * @param view
     */
    public void onSearchButton(final View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        final String q = ((EditText) mSearchView.findViewById(R.id.q)).getText().toString();
        final Bundle bundle = new Bundle();
        bundle.putString("q", q);
        getSupportLoaderManager().initLoader(0, bundle, this);
    }

    private Repositorie[] parseJson(final String json) {
        try {
            final JSONArray jsons = (JSONArray) new JSONObject(json).getJSONArray("repositories");
            final int size = jsons.length();
            final Repositorie[] repositories = new Repositorie[size];
            for (int i = 0; i < size; i++) {
                repositories[i] = new Repositorie(jsons.getJSONObject(i));
            }
            return repositories;
        } catch (final JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void initSearchResultListView() {
        mSearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent)
                        .getAdapter()).getItem(position);
                final Refs refs = new Refs(repositorie.get(Repositorie.OWNER), repositorie
                        .get(Repositorie.NAME), "master", "master");
                startActivity(new Intent(getApplicationContext(), BlobsActivity.class).putExtra(
                        Extra.REFS, refs));
            }
        });

        mSearchResultListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent)
                        .getAdapter()).getItem(position);
                startActivity(new Intent(getApplicationContext(), RepositorieInfoActivity.class)
                        .putExtra(Extra.REPOSITORIE, repositorie));
                return false;
            }
        });
    }

    @Override
    public Loader<RepositorieAdapter> onCreateLoader(final int id, final Bundle bundle) {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.now_loading), true,
                true, new OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        getSupportLoaderManager().destroyLoader(0);
                    }
                });
        final AsyncTaskLoader<RepositorieAdapter> asyncTaskLoader = new AsyncTaskLoader<RepositorieAdapter>(
                getBaseContext()) {
            @Override
            public RepositorieAdapter loadInBackground() {
                final GitHubAPI github = new GitHubAPI();
                github.goStealth();
                final Repository repository = new Repository(github);
                final Response response = repository.search(bundle.getString("q"));
                final String resultJson = response.resp;
                final Repositorie[] repositories = (null == resultJson) ? null
                        : parseJson(resultJson);
                return new RepositorieAdapter(getBaseContext(),
                        android.R.layout.simple_list_item_2, repositories);
            }
        };
        asyncTaskLoader.forceLoad();

        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(final Loader<RepositorieAdapter> loader,
            final RepositorieAdapter repositorieAdapter) {
        mSearchResultListView.setAdapter(repositorieAdapter);
        mProgressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(final Loader<RepositorieAdapter> loader) {
    }

    static class FileSort implements Comparator<File> {
        public int compare(final File file1, final File file2) {
            return file1.getName().compareTo(file2.getName());
        }
    }
}