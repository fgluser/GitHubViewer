package net.flaxia.android.githubviewer;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends Activity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    public static final String Q = "q";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        onSearch(getIntent().getExtras().getString(Q));
    }

    /**
     * 検索ボタンが押されたときの処理
     * 
     * @param view
     */
    public void onSearchButton(View view) {
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
        if (null != resultJson) {
            Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        parseJson(resultJson);
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
