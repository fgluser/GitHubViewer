package net.flaxia.android.githubviewer;

import org.idlesoft.libraries.ghapi.GitHubAPI;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class CodeViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
        GitHubAPI ghapi = new GitHubAPI();
        Bundle extras = getIntent().getExtras();
        ((WebView) findViewById(R.id.webview)).loadDataWithBaseURL("about:blank", ghapi.object.raw(
                extras.getString("owner"), extras.getString("name"), extras.getString("sha")).resp,
                "text/html", "utf-8", null);
    }
}
