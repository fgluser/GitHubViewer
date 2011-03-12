package net.flaxia.android.githubviewer;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

public class SearchActivity extends Activity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    public static final String Q = "q";

    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search);

        initListView();
        onSearch(getIntent().getExtras().getString(Q));
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.repositorie);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent).getAdapter())
                        .getRepositorie(position);
                startActivity(new Intent(getApplicationContext(), BlobsActivity.class).putExtra(
                        BlobsActivity.REPOSITORIE, repositorie));
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
        onSearch(q);
    }

    private void onSearch(String q) {
        if (CommonHelper.isEmpty(q)) {
            Toast.makeText(getApplicationContext(), R.string.search_word_is_empty,
                    Toast.LENGTH_LONG).show();
            return;
        }
        String resultJson = executeSearch(q);
        if (null == resultJson) {
            Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Repositorie[] repositories = parseJson(resultJson);
        Log.d(TAG, "取得したリポジトリ数: " + repositories.length);
        mListView.setAdapter(new RepositorieAdapter(this, android.R.layout.simple_list_item_2,
                repositories));
        Log.d(TAG, mListView.getCount());
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
        Log.d(TAG, response.url);
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
