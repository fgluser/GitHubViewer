package net.flaxia.android.githubviewer;

import java.util.Iterator;

import net.flaxia.android.githubviewer.adapter.BranchesTagsAdapter;
import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.model.Repositorie;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RepositorieInfoActivity extends BaseAsyncActivity {
    private Spinner mBranchesSpinner;
    private Spinner mTagsSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositorie_info);
        Repositorie repositorie = (Repositorie) getIntent().getExtras().getSerializable(
                Extra.REPOSITORIE);
        initSpinners();
        initInformation();
        doAsyncTask(repositorie.get(Repositorie.OWNER), repositorie.get(Repositorie.NAME));
    }

    private void initSpinners() {
        mBranchesSpinner = (Spinner) findViewById(R.id.branches);
        mBranchesSpinner.setPrompt(getText(R.string.would_you_like_to_which));
        mTagsSpinner = (Spinner) findViewById(R.id.tags);
        mTagsSpinner.setPrompt(getText(R.string.would_you_like_to_which));
    }

    private void initInformation() {
        Repositorie repositorie = (Repositorie) getIntent().getExtras().getSerializable(
                Extra.REPOSITORIE);
        ((TextView) findViewById(R.id.owner_repositorie)).setText(repositorie
                .get(Repositorie.OWNER)
                + " / " + repositorie.get(Repositorie.NAME));
        ((TextView) findViewById(R.id.homepage)).setText(repositorie.get(Repositorie.HOMEPAGE));
        ((TextView) findViewById(R.id.description)).setText(repositorie
                .get(Repositorie.DESCRIPTION));
    }

    /**
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
                    BranchesTagsAdapter branchesAdapter = new BranchesTagsAdapter(
                            RepositorieInfoActivity.this, android.R.layout.simple_spinner_item);
                    branchesAdapter.add(new KeyValuePair(getString(R.string.branches), null));
                    for (KeyValuePair branche : branches) {
                        branchesAdapter.add(branche);
                    }
                    mBranchesSpinner.setAdapter(branchesAdapter);
                }
            };
            mHandler.post(runnable);
        }
    }

    /**
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
                    BranchesTagsAdapter tagsAdapter = new BranchesTagsAdapter(
                            RepositorieInfoActivity.this, android.R.layout.simple_spinner_item);
                    tagsAdapter.add(new KeyValuePair(getText(R.string.tags).toString(), null));
                    for (KeyValuePair tag : tags) {
                        tagsAdapter.add(tag);
                    }
                    mTagsSpinner.setAdapter(tagsAdapter);
                }
            };
        }
        mHandler.post(runnable);
    }

    @Override
    protected void executeAsyncTask(String... parameters) {
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

    private KeyValuePair[] executeGetTags(String owner, String name) {
        GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        String json = ghapi.repo.tags(owner, name).resp;
        if (null == json) {
            return null;
        }
        try {
            return jsonToKeyValuePairs(new JSONObject(json).getJSONObject("tags"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private KeyValuePair[] executeGetBranches(String owner, String name) {
        GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        String json = ghapi.repo.branches(owner, name).resp;
        if (null == json) {
            return null;
        }
        try {
            return jsonToKeyValuePairs(new JSONObject(json).getJSONObject("branches"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private KeyValuePair[] jsonToKeyValuePairs(JSONObject jsonObject) {
        int size = jsonObject.length();
        KeyValuePair[] keyValuePairs = new KeyValuePair[size];
        int i = 0;
        for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
            String key = (String) iterator.next();
            try {
                System.out.println("key: " + key);
                keyValuePairs[i] = new KeyValuePair(key, jsonObject.getString(key));
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return keyValuePairs;
    }
}
