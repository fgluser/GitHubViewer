
package net.flaxia.android.githubviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Extra;

import org.idlesoft.libraries.ghapi.GitHubAPI;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CodeViewActivity extends FragmentActivity implements LoaderCallbacks<String> {
    private static final int MENU_COPY = Menu.FIRST + 1;
    protected WebView mWebView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);
        initWebView();
        final String url = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        if (null == url || 0 == url.length()) {
            getSupportLoaderManager().initLoader(0, null, this);
        } else {
            mWebView.loadUrl(url);
        }
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
                mProgressDialog = ProgressDialog.show(CodeViewActivity.this, null,
                        getString(R.string.rendering), true, true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                if (null != mProgressDialog && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mWebView.clearCache(true);

            }
        });
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

    @Override
    public Loader<String> onCreateLoader(int arg0, Bundle arg1) {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.now_loading), true,
                true, new OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        getSupportLoaderManager().destroyLoader(0);
                        finish();
                    }
                });
        final AsyncTaskLoader<String> asyncTaskLoader = new AsyncTaskLoader<String>(
                getBaseContext()) {
            private String getSource() throws IOException {
                final Bundle extras = getIntent().getExtras();
                final String path = extras.getString(Extra.EXPLORER_PATH);
                if (null == path || 0 == path.length()) {
                    return new GitHubAPI().object.raw(extras.getString("owner"),
                            extras.getString("name"), extras.getString("sha")).resp;
                }
                final StringBuilder sb = new StringBuilder();
                final BufferedReader br = new BufferedReader(new FileReader(path));
                while (br.ready()) {
                    sb.append(br.readLine() + "\n");
                }
                br.close();

                return sb.toString();
            }

            private String getHtml() throws IOException {
                final InputStream inputStream = getResources().getAssets().open("html");
                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                final StringBuilder stringBuilder = new StringBuilder();
                while (bufferedReader.ready()) {
                    stringBuilder.append(bufferedReader.readLine() + "\n");
                }
                return stringBuilder.toString();
            }

            private String replace(final String source, final Map<String, String> map) {
                if (map.isEmpty()) {
                    return source;
                }
                final String key = map.keySet().iterator().next();
                final String value = map.remove(key);

                return replace(source.replaceFirst(key, value), map);
            }

            @Override
            public String loadInBackground() {
                final Bundle extras = getIntent().getExtras();
                try {
                    final String source = getSource();
                    final String html = getHtml();

                    final Map<String, String> map = new HashMap<String, String>();
                    map.put("@title", extras.getString("fileName"));
                    map.put("@source", CommonHelper.escapeSign(source));
                    map.put("@theme",
                            PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                                    .getString(ConfigureActivity.CODE_THEME, "default"));

                    return replace(html, map);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                return getString(R.string.failed_to_retrieve_the_information);
            }
        };
        asyncTaskLoader.forceLoad();

        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<String> arg0, String arg1) {
        mProgressDialog.dismiss();
        mWebView.loadDataWithBaseURL("about:blank", arg1, "text/html", "utf-8", null);
    }

    @Override
    public void onLoaderReset(Loader<String> arg0) {
    }
}
