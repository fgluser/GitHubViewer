package net.flaxia.android.githubviewer;

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
        GitHubAPI ghapi = new GitHubAPI();
        Bundle extras = getIntent().getExtras();
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadDataWithBaseURL("about:blank", head()
                + ghapi.object.raw(extras.getString("owner"), extras.getString("name"), extras
                        .getString("sha")).resp + foot(), "text/html", "utf-8", null);
        System.out.println(webView.toString());
    }

    public String head() {
        return "<script type=\"text/javascript\" src=\"file:///android_asset/scripts/shCore.js\"></script>"
                + "<script type=\"text/javascript\" src=\"file:///android_asset/scripts/shBrushPython.js\"></script>"
                + "<link href=\"file:///android_asset/styles/shCore.css\" rel=\"stylesheet\" type=\"text/css\" />"
                + "<link href=\"file:///android_asset/styles/shThemeDefault.css\" rel=\"stylesheet\" type=\"text/css\" />"
                + "<pre class=\"brush: python\">";
    }

    public String foot() {
        return "</pre><script type=\"text/javascript\">SyntaxHighlighter.all()</script>";
    }
}
