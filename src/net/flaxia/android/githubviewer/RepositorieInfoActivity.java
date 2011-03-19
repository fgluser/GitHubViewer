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
        mTagsSpinner = (Spinner) findViewById(R.id.tags);
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

    @Override
    protected void executeAsyncTask(String... parameters) {
        final KeyValuePair[] tags = executeGetTags(parameters[0], parameters[1]);
        final KeyValuePair[] branches = executeGetBranches(parameters[0], parameters[1]);
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
                    BranchesTagsAdapter branchesTagsAdapter = new BranchesTagsAdapter(
                            RepositorieInfoActivity.this, android.R.layout.simple_spinner_item,
                            new KeyValuePair[] { (new KeyValuePair(RepositorieInfoActivity.this
                                    .getString(R.string.select_branche), "")) });
                    for (KeyValuePair branche : branches) {
                        // branchesTagsAdapter.add(branche);
                    }
                    mBranchesSpinner.setAdapter(branchesTagsAdapter);
                }
            };
        }
        mHandler.post(runnable);

        if (null == tags) {
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
                    BranchesTagsAdapter branchesTagsAdapter = new BranchesTagsAdapter(
                            RepositorieInfoActivity.this, android.R.layout.simple_spinner_item,
                            new KeyValuePair[] { (new KeyValuePair(RepositorieInfoActivity.this
                                    .getString(R.string.select_tag), "")) });
                    for (KeyValuePair tag : tags) {
                        // branchesTagsAdapter.add(tag);
                    }
                    mTagsSpinner.setAdapter(branchesTagsAdapter);
                }
            };
        }
        mHandler.post(runnable);
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
                keyValuePairs[i] = new KeyValuePair(key, jsonObject.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return keyValuePairs;
    }
}
