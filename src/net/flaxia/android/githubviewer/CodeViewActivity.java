package net.flaxia.android.githubviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.flaxia.android.githubviewer.util.CommonHelper;

import org.idlesoft.libraries.ghapi.GitHubAPI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CodeViewActivity extends Activity {
    Handler handler = new Handler();
    WebView mWebView;
    LoadingDialog mLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
        mLoadingDialog = new LoadingDialog(this);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mLoadingDialog = new LoadingDialog(CodeViewActivity.this);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLoadingDialog.dismiss();
            }
        });
        task();
    }

    public void task() {
        new Thread(new Runnable() {
            public void run() {
                final String html = createHtml();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        mWebView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8",
                                null);
                    }
                });
            }
        }).start();
    }

    public String createHtml() {
        String html;
        GitHubAPI ghapi = new GitHubAPI();
        Bundle extras = getIntent().getExtras();
        String source = ghapi.object.raw(extras.getString("owner"), extras.getString("name"),
                extras.getString("sha")).resp;
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
            html = html.replaceFirst("@source", CommonHelper.escapeSign(source));
        }

        return html;
    }
}
