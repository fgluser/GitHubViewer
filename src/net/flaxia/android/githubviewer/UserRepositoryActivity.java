
package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.adapter.RepositorieAdapter;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Repositorie;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class UserRepositoryActivity extends BaseAsyncActivity {
    private static final String TAG = UserRepositoryActivity.class.getSimpleName();

    private ListView mListView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_repository);
        initListView();
        doAsyncTask(getIntent().getExtras().getString(Extra.USERNAME));
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.repositorie);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent)
                        .getAdapter())
                        .getItem(position);
                final Refs refs = new Refs(repositorie.get(Repositorie.OWNER), repositorie
                        .get(Repositorie.NAME), "master", "master");
                startActivity(new Intent(getApplicationContext(), BlobsActivity.class).putExtra(
                        Extra.REFS, refs));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Repositorie repositorie = ((RepositorieAdapter) ((ListView) parent)
                        .getAdapter())
                        .getItem(position);
                startActivity(new Intent(getApplicationContext(), RepositorieInfoActivity.class)
                        .putExtra(Extra.REPOSITORIE, repositorie));

                return false;
            }
        });
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        final String resultJson = executeSearch(parameters[0]);
        final Repositorie[] repositories = (null == resultJson) ? null : parseJson(resultJson);
        final Runnable runnable;
        if (null == repositories) {
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
                    mListView.setAdapter(new RepositorieAdapter(UserRepositoryActivity.this,
                            android.R.layout.simple_list_item_2, repositories));
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
    private String executeSearch(final String username) {
        final GitHubAPI github = new GitHubAPI();
        github.goStealth();
        final Response response = github.user.watching(username);
        LogEx.d(TAG, response.url);

        return response.resp;
    }

    /**
     * @param json
     * @return
     */
    private Repositorie[] parseJson(final String json) {
        try {
            final JSONArray jsons = (JSONArray) new JSONObject(json).getJSONArray("repositories");
            final int size = jsons.length();
            final Repositorie[] repositories = new Repositorie[size];
            for (int i = 0; i < size; i++) {
                repositories[i] = new Repositorie(jsons.getJSONObject(i));
            }

            return repositories;
        } catch (final JSONException e) {
            e.printStackTrace();

            return null;
        }
    }
}
