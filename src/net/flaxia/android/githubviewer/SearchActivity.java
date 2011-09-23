
package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.adapter.RepositorieAdapter;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Repositorie;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;
import net.flaxia.android.githubviewer.util.RepositoryEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends BaseAsyncActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private ListView mListView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initListView();
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.repositorie);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent)
                        .getAdapter())
                        .getItem(position);
                final Refs refs = new Refs(repositorie.get(Repositorie.OWNER), repositorie
                        .get(Repositorie.NAME), "master", "master");
                startActivity(new Intent(getApplicationContext(), BlobsActivity.class).putExtra(
                        Extra.REFS, refs));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent)
                        .getAdapter())
                        .getItem(position);
                startActivity(new Intent(getApplicationContext(), RepositorieInfoActivity.class)
                        .putExtra(Extra.REPOSITORIE, repositorie));
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

        final String q = ((EditText) findViewById(R.id.q)).getText().toString();
        final String language = ((EditText) findViewById(R.id.language)).getText().toString();

        doAsyncTask(q, language);
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        final String resultJson = executeSearch(parameters[0], parameters[1]);
        final Repositorie[] repositories = (null == resultJson) ? null : parseJson(resultJson);
        Runnable runnable;
        if (null == repositories) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                            Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    mListView.setAdapter(new RepositorieAdapter(SearchActivity.this,
                            android.R.layout.simple_list_item_2, repositories));
                }
            };
        }
        mHandler.post(runnable);
    }

    /**
     * 検索を行ない，結果をjson形式の文字列で返す
     * 
     * @param q
     * @return
     */
    private String executeSearch(final String q, final String language) {
        final GitHubAPI github = new GitHubAPI();
        github.goStealth();
        final RepositoryEx repositoryEx = new RepositoryEx(github);
        final Response response = repositoryEx.search(q, language);
        LogEx.d(TAG, response.url);

        return response.resp;
    }

    /**
     * @param json
     * @return
     */
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
}
