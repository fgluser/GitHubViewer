
package net.flaxia.android.githubviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CodeViewActivity extends BaseAsyncActivity {
    private static final String TAG = CodeViewActivity.class.getSimpleName();
    private static final int MENU_COPY = Menu.FIRST + 1;
    protected WebView mWebView;
    private ProgressDialog mRenderingDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
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
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                mRenderingDialog = ProgressDialog.show(CodeViewActivity.this, null,
                        getString(R.string.rendering), true, true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                if (null != mRenderingDialog && mRenderingDialog.isShowing()) {
                    mRenderingDialog.dismiss();
                }
                mWebView.clearCache(true);

            }
        });
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        final String html = createHtml();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
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
        final Bundle extras = getIntent().getExtras();
        final String path = extras.getString(Extra.EXPLORER_PATH);
        String source;
        if (null == path || 0 == path.length()) {
            source = new GitHubAPI().object.raw(extras.getString("owner"),
                    extras.getString("name"), extras.getString("sha")).resp;
        } else {
            try {
                final BufferedReader br = new BufferedReader(new FileReader(path));
                final StringBuilder sb = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str + "\n");
                }
                source = sb.toString();
            } catch (final IOException e) {
                e.printStackTrace();
                source = "";
            }
        }
        final StringBuilder sb = new StringBuilder();
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
        } catch (final IOException e) {
            e.printStackTrace();
            html = null;
        }
        try {
            if (null == source || null == html) {
                html = getResources().getString(R.string.failed_to_retrieve_the_information);
            } else {
                html = html.replaceFirst("@title", extras.getString("fileName"));
                html = html.replaceFirst("@source", CommonHelper.escapeSign(source));
                final String theme = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()).getString(ConfigureActivity.CODE_THEME, "default");
                html = html.replaceFirst("@theme", theme);
                LogEx.d(TAG, theme);
                LogEx.d(TAG, source);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            html = "";
        }

        return html;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(Menu.NONE, MENU_COPY, Menu.NONE, R.string.copy).setIcon(R.drawable.copy);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case MENU_COPY:
                copyText();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void copyText() {
        final KeyEvent keyEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
        keyEvent.dispatch(mWebView);
    }

}
