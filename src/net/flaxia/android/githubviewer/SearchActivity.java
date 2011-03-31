package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.adapter.RepositorieAdapter;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Repositorie;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends BaseAsyncActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private static final String REPOSITORIE = "repositorie";
    private ListView mListView;
    private Repositorie[] mRepositories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search);
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mRepositories) {
            String q = getIntent().getExtras().getString(Extra.Q);
            ((EditText) findViewById(R.id.q)).setText(q);
            doAsyncTask(q);
        } else {
            mListView.setAdapter(new RepositorieAdapter(this, android.R.layout.simple_list_item_2,
                    mRepositories));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(REPOSITORIE, mRepositories);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRepositories = (Repositorie[]) savedInstanceState.getSerializable(REPOSITORIE);
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.repositorie);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent).getAdapter())
                        .getItem(position);
                Refs refs = new Refs(repositorie.get(Repositorie.OWNER), repositorie
                        .get(Repositorie.NAME), "master", "master");
                startActivity(new Intent(getApplicationContext(), BlobsActivity.class).putExtra(
                        Extra.REFS, refs));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent).getAdapter())
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
    public void onSearchButton(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        String q = ((EditText) findViewById(R.id.q)).getText().toString();
        doAsyncTask(q);
    }

    @Override
    protected void executeAsyncTask(String... parameters) {
        String resultJson = executeSearch(parameters[0]);
        mRepositories = (null == resultJson) ? null : parseJson(resultJson);
        Runnable runnable;
        if (null == mRepositories) {
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
                            android.R.layout.simple_list_item_2, mRepositories));
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
    private String executeSearch(String q) {
        GitHubAPI github = new GitHubAPI();
        github.goStealth();
        Response response = github.repo.search(q);
        LogEx.d(TAG, response.url);
        return response.resp;
    }

    /**
     * 
     * @param json
     * @return
     */
    private Repositorie[] parseJson(String json) {
        try {
            JSONArray jsons = (JSONArray) new JSONObject(json).getJSONArray("repositories");
            int size = jsons.length();
            Repositorie[] repositories = new Repositorie[size];
            for (int i = 0; i < size; i++) {
                repositories[i] = new Repositorie(jsons.getJSONObject(i));
            }
            return repositories;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
