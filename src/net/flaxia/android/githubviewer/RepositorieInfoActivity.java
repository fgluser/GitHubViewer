
package net.flaxia.android.githubviewer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.flaxia.android.githubviewer.adapter.BranchesTagsAdapter;
import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Repositorie;
import net.flaxia.android.githubviewer.util.Extra;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RepositorieInfoActivity extends BaseActivity implements
        LoaderCallbacks<Map<String, KeyValuePair[]>> {
    private Spinner mBranchesSpinner;
    private Spinner mTagsSpinner;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositorie_info);
        final Repositorie repositorie = (Repositorie) getIntent().getExtras().getSerializable(
                Extra.REPOSITORIE);
        initSpinners();
        initInformation();
        final Bundle bundle = new Bundle();
        bundle.putString("owner", repositorie.get(Repositorie.OWNER));
        bundle.putString("name", repositorie.get(Repositorie.NAME));
        getSupportLoaderManager().initLoader(0, bundle, this);
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
        setTitle(repositorie.get(Repositorie.OWNER) + " / " + repositorie.get(Repositorie.NAME));
        ((TextView) findViewById(R.id.homepage)).setText(repositorie.get(Repositorie.HOMEPAGE));
        ((TextView) findViewById(R.id.description)).setText(repositorie
                .get(Repositorie.DESCRIPTION));
    }

    /**
     * ブランチのアダプタを作成する
     * 
     * @param branches
     */
    private BranchesTagsAdapter makeBranchesAdapter(final KeyValuePair[] branches) {
        final BranchesTagsAdapter branchesAdapter = new BranchesTagsAdapter(
                RepositorieInfoActivity.this, android.R.layout.simple_spinner_item);
        branchesAdapter.add(new KeyValuePair(getString(R.string.branches), null));
        for (final KeyValuePair branche : branches) {
            branchesAdapter.add(branche);
        }
        return branchesAdapter;
    }

    /**
     * タグのアダプターを作成する
     * 
     * @param tags
     */
    private BranchesTagsAdapter makeTagsAdapter(final KeyValuePair[] tags) {
        final BranchesTagsAdapter tagsAdapter = new BranchesTagsAdapter(
                RepositorieInfoActivity.this, android.R.layout.simple_spinner_item);
        tagsAdapter.add(new KeyValuePair(getText(R.string.tags).toString(), null));
        for (final KeyValuePair tag : tags) {
            tagsAdapter.add(tag);
        }
        return tagsAdapter;
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

    @Override
    public Loader<Map<String, KeyValuePair[]>> onCreateLoader(final int id, final Bundle args) {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.now_loading), true,
                true, new OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        getSupportLoaderManager().destroyLoader(0);
                    }
                });
        final AsyncTaskLoader<Map<String, KeyValuePair[]>> asyncTaskLoader =
                new AsyncTaskLoader<Map<String, KeyValuePair[]>>(getBaseContext()) {
                    @Override
                    public Map<String, KeyValuePair[]> loadInBackground() {
                        final Map<String, KeyValuePair[]> map = new HashMap<String, KeyValuePair[]>();
                        final String owner = args.getString("owner");
                        final String name = args.getString("name");
                        map.put("tags", executeGetTags(owner, name));
                        map.put("branches", executeGetBranches(owner, name));

                        return map;
                    }
                };
        asyncTaskLoader.forceLoad();

        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(final Loader<Map<String, KeyValuePair[]>> loader,
            final Map<String, KeyValuePair[]> map) {
        final KeyValuePair[] tags = map.get("tags");
        final KeyValuePair[] branches = map.get("branches");
        if (null == branches || null == tags) {
            Toast.makeText(getApplicationContext(), R.string.could_not_get_the_results,
                    Toast.LENGTH_LONG).show();
        } else {
            mBranchesSpinner.setAdapter(makeBranchesAdapter(branches));
            mTagsSpinner.setAdapter(makeTagsAdapter(tags));
        }
        mProgressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(final Loader<Map<String, KeyValuePair[]>> arg0) {
    }
}
