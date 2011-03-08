package net.flaxia.android.githubviewer;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;

import android.app.Activity;
import android.os.Bundle;

public class SearchActivity extends Activity {
    public static final String Q = "q";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        GitHubAPI github = new GitHubAPI();
        github.goStealth();
        Response res = github.repo.search((String) getIntent().getExtras().get(Q));
        System.out.println(res.resp.toString());
    }
}
