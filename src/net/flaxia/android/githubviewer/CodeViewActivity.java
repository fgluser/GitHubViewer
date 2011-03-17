package net.flaxia.android.githubviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.flaxia.android.githubviewer.util.CommonHelper;

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
            String fileName = extras.getString("fileName");
            html = html.replaceFirst("@title", fileName);
            String suffix = CommonHelper.getSuffix(fileName);
            if (null == suffix) {
                suffix = "plain";
            }
            
            html = html.replaceFirst("@js", CommonHelper.getBrushName(suffix));
            html = html.replaceFirst("@lang", CommonHelper.getLanguageName(suffix));
            html = html.replaceFirst("@body", source);
        }
        webView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
    }
    

}
