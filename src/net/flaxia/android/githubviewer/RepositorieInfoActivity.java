
package net.flaxia.android.githubviewer;

import java.util.Iterator;

import net.flaxia.android.githubviewer.adapter.BranchesTagsAdapter;
import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Repositorie;
import net.flaxia.android.githubviewer.util.Extra;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RepositorieInfoActivity extends BaseAsyncActivity {
    private Spinner mBranchesSpinner;
    private Spinner mTagsSpinner;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositorie_info);
        final Repositorie repositorie = (Repositorie) getIntent().getExtras().getSerializable(
                Extra.REPOSITORIE);
        initSpinners();
        initInformation();
        doAsyncTask(repositorie.get(Repositorie.OWNER), repositorie.get(Repositorie.NAME));
    }

    /**
     * スピナーの初期化
     */
    private void initSpinners() {
        mBranchesSpinner = (Spinner) findViewById(R.id.branches);
        mBranchesSpinner.setPrompt(getText(R.string.would_you_like_to_which));
        mBranchesSpinner.setOnItemSelectedListener(createOnItemSelectedListener());

        mTagsSpinner = (Spinner) findViewById(R.id.tags);
        mTagsSpinner.setPrompt(getText(R.string.would_you_like_to_which));
        mTagsSpinner.setOnItemSelectedListener(createOnItemSelectedListener());
    }

    private AdapterView.OnItemSelectedListener createOnItemSelectedListener() {
        final AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                if (0 == position) {
                    return;
                }
                final Repositorie repositorie = (Repositorie) getIntent().getExtras()
                        .getSerializable(
                                Extra.REPOSITORIE);
                final KeyValuePair keyValuePair = (KeyValuePair) ((Spinner) parent).getAdapter()
                        .getItem(
                                position);
                final Intent intent = new Intent(getApplicationContext(), BlobsActivity.class);
                final Refs refs = new Refs(repositorie.get(Repositorie.OWNER), repositorie
                        .get(Repositorie.NAME), keyValuePair.getKey(), keyValuePair.getValue());
                intent.putExtra(Extra.REFS, refs);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0) {
            }
        };

        return listener;
    }

    /**
     * 初期化処理
     */
    private void initInformation() {
        final Repositorie repositorie = (Repositorie) getIntent().getExtras().getSerializable(
                Extra.REPOSITORIE);
        ((TextView) findViewById(R.id.owner_repositorie)).setText(repositorie
                .get(Repositorie.OWNER)
                + " / " + repositorie.get(Repositorie.NAME));
        ((TextView) findViewById(R.id.homepage)).setText(repositorie.get(Repositorie.HOMEPAGE));
        ((TextView) findViewById(R.id.description)).setText(repositorie
                .get(Repositorie.DESCRIPTION));
    }

    /**
     * ブランチのアダプタを作成する
     * 
     * @param branches
     */
    private void makeBranchesAdapter(final KeyValuePair[] branches) {
        Runnable runnable;
        if (null == branches) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                            Toast.LENGTH_SHORT).show();
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    final BranchesTagsAdapter branchesAdapter = new BranchesTagsAdapter(
                            RepositorieInfoActivity.this, android.R.layout.simple_spinner_item);
                    branchesAdapter.add(new KeyValuePair(getString(R.string.branches), null));
                    for (final KeyValuePair branche : branches) {
                        branchesAdapter.add(branche);
                    }
                    mBranchesSpinner.setAdapter(branchesAdapter);
                }
            };
            mHandler.post(runnable);
        }
    }

    /**
     * タグのアダプターを作成する
     * 
     * @param tags
     */
    private void makeTagsAdapter(final KeyValuePair[] tags) {
        Runnable runnable;
        if (null == tags) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                            Toast.LENGTH_SHORT).show();
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    final BranchesTagsAdapter tagsAdapter = new BranchesTagsAdapter(
                            RepositorieInfoActivity.this, android.R.layout.simple_spinner_item);
                    tagsAdapter.add(new KeyValuePair(getText(R.string.tags).toString(), null));
                    for (final KeyValuePair tag : tags) {
                        tagsAdapter.add(tag);
                    }
                    mTagsSpinner.setAdapter(tagsAdapter);
                }
            };
        }
        mHandler.post(runnable);
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        final KeyValuePair[] tags = executeGetTags(parameters[0], parameters[1]);
        final KeyValuePair[] branches = executeGetBranches(parameters[0], parameters[1]);

        makeBranchesAdapter(branches);
        makeTagsAdapter(tags);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        });
    }

    /**
     * タグ一覧取得のAPIを実行する
     * 
     * @param owner
     * @param name
     * @return
     */
    private KeyValuePair[] executeGetTags(final String owner, final String name) {
        final GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        final String json = ghapi.repo.tags(owner, name).resp;
        if (null == json) {
            return null;
        }
        try {
            return jsonToKeyValuePairs(new JSONObject(json).getJSONObject("tags"));
        } catch (final JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ブランチ一覧取得のAPIを実行する
     * 
     * @param owner
     * @param name
     * @return
     */
    private KeyValuePair[] executeGetBranches(final String owner, final String name) {
        final GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        final String json = ghapi.repo.branches(owner, name).resp;
        if (null == json) {
            return null;
        }
        try {
            return jsonToKeyValuePairs(new JSONObject(json).getJSONObject("branches"));
        } catch (final JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JSONをKeyValuePairの配列にする
     * 
     * @param jsonObject
     * @return
     */
    private KeyValuePair[] jsonToKeyValuePairs(final JSONObject jsonObject) {
        final int size = jsonObject.length();
        final KeyValuePair[] keyValuePairs = new KeyValuePair[size];
        int i = 0;
        for (final Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
            final String key = (String) iterator.next();
            try {
                keyValuePairs[i] = new KeyValuePair(key, jsonObject.getString(key));
                i++;
            } catch (final JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return keyValuePairs;
    }
}
