
package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.adapter.UserAdapter;
import net.flaxia.android.githubviewer.model.User;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class UserSearchActivity extends BaseAsyncActivity {
    private static final String TAG = UserSearchActivity.class.getSimpleName();

    private ListView mListView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        initListView();
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.user);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final User user = ((UserAdapter) ((ListView) parent).getAdapter())
                        .getItem(position);
                startActivity(new Intent(getApplicationContext(), UserRepositoryActivity.class)
                        .putExtra(
                                Extra.USERNAME, user.get(User.USERNAME)));
            }
        });
    }

    /**
     * 検索ボタンが押されたときの処理
     * 
     * @param view
     */
    public void onSearchButton(final View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        final String q = ((EditText) findViewById(R.id.q)).getText().toString();
        doAsyncTask(q);
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        final String resultJson = executeSearch(parameters[0]);
        final User[] users = (null == resultJson) ? null : parseJson(resultJson);
        final Runnable runnable;
        if (null == users) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                            Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    mListView.setAdapter(new UserAdapter(UserSearchActivity.this,
                            android.R.layout.simple_list_item_1, users));
                }
            };
        }
        mHandler.post(runnable);
    }

    /**
     * 検索を行ない，結果をjson形式の文字列で返す
     * 
     * @param q
     * @return
     */
    private String executeSearch(final String q) {
        final GitHubAPI github = new GitHubAPI();
        github.goStealth();
        final Response response = github.user.search(q);
        LogEx.d(TAG, response.url);

        return response.resp;
    }

    /**
     * @param json
     * @return
     */
    private User[] parseJson(final String json) {
        try {
            final JSONArray jsons = (JSONArray) new JSONObject(json).getJSONArray("users");
            final int size = jsons.length();
            final User[] users = new User[size];
            for (int i = 0; i < size; i++) {
                users[i] = new User(jsons.getJSONObject(i));
            }
            return users;
        } catch (final JSONException e) {
            e.printStackTrace();

            return null;
        }
    }
}
