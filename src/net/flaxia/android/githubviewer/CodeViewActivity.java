package net.flaxia.android.githubviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.idlesoft.libraries.ghapi.GitHubAPI;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CodeViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setBuiltInZoomControls(true);
        GitHubAPI ghapi = new GitHubAPI();
        Bundle extras = getIntent().getExtras();
        String source = ghapi.object.raw(extras.getString("owner"), extras.getString("name"),
                extras.getString("sha")).resp;
        String html;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            try {
                br = new BufferedReader(new InputStreamReader(getResources().getAssets().open(
                        "html")));
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str + "\n");
                }
            } finally {
                if (br != null)
                    br.close();
            }
            html = sb.toString();
        } catch (IOException e) {
            html = null;
        }
        if (null == source || null == html) {
            html = getResources().getString(R.string.failed_to_retrieve_the_information);
        } else {
            html = html.replaceFirst("@title", extras.getString("fileName"));
            html = html.replaceFirst("@source", source);
        }
        webView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
    }
}
