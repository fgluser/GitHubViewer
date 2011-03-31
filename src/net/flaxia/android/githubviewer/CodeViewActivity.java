package net.flaxia.android.githubviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CodeViewActivity extends BaseAsyncActivity {
    private static final String TAG = CodeViewActivity.class.getSimpleName();
    protected WebView mWebView;
    private LoadingDialog mRenderingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
        initWebView();
        doAsyncTask();
    }

    /**
     * WebViewの初期化
     */
    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mRenderingDialog = new LoadingDialog(CodeViewActivity.this, R.string.rendering);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (null != mRenderingDialog && mRenderingDialog.isShowing()) {
                    mRenderingDialog.dismiss();
                }
                mWebView.clearCache(true);
            }
        });
    }

    @Override
    protected void executeAsyncTask(String... parameters) {
        final String html = createHtml();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
                mWebView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
            }
        });
    }

    /**
     * 表示するHTMLを生成する
     * 
     * @return
     */
    private String createHtml() {
        String html;
        Bundle extras = getIntent().getExtras();
        String path = extras.getString(Extra.EXPLORER_PATH);
        String source;
        if (null == path || 0 == path.length()) {
            source = new GitHubAPI().object.raw(extras.getString("owner"),
                    extras.getString("name"), extras.getString("sha")).resp;
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str + "\n");
                }
                source = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                source = "";
            }
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(getResources().getAssets().open(
                        "html")));
                for (String str = br.readLine(); null != str; str = br.readLine()) {
                    sb.append(str + "\n");
                }
            } finally {
                if (null != br) {
                    br.close();
                }
            }
            html = sb.toString();
        } catch (IOException e) {
            html = null;
        }
        try {
            if (null == source || null == html) {
                html = getResources().getString(R.string.failed_to_retrieve_the_information);
            } else {
                html = html.replaceFirst("@title", extras.getString("fileName"));
                html = html.replaceFirst("@source", CommonHelper.escapeSign(source));
                LogEx.d(TAG, source);
            }
        } catch (Exception e) {
            e.printStackTrace();
            html = "";
        }

        return html;
    }
}
