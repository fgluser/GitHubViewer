package net.flaxia.android.githubviewer;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends Activity {
    public static final String Q = "q";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        String q = getIntent().getExtras().getString(Q);
        if (CommonHelper.isEmpty(q)) {
            Toast.makeText(getApplicationContext(), R.string.search_word_is_empty,
                    Toast.LENGTH_LONG).show();
            return;
        }
        System.out.println(executeSearch(q));
    }

    /**
     * 検索ボタンが押されたときの処理
     * @param view
     */
    public void onSearchButton(View view) {
        String q = ((EditText) findViewById(R.id.q)).getText().toString();
        if (CommonHelper.isEmpty(q)) {
            Toast.makeText(getApplicationContext(), R.string.search_word_is_empty,
                    Toast.LENGTH_LONG).show();
            return;
        }
        executeSearch(q);
    }

    private String executeSearch(String q) {
        GitHubAPI github = new GitHubAPI();
        github.goStealth();
        Response res = github.repo.search(q);
        if (999 == res.statusCode) {
            return null;
        }
        return res.resp;
    }
}
