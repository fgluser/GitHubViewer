
package net.flaxia.android.githubviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import net.flaxia.android.githubviewer.adapter.KeyValuePairAdapter;
import net.flaxia.android.githubviewer.adapter.TreeAdapter;
import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Tree;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class BlobsActivity extends BaseAsyncActivity {
    private static final String TAG = BlobsActivity.class.getSimpleName();
    private TreeAdapter mSpinnerAdapter;
    private Tree mTree;
    private ListView mListView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blobs);
        final Refs refs = (Refs) getIntent().getExtras().getSerializable(Extra.REFS);
        final String owner = refs.getOwner();
        final String name = refs.getName();
        final String hash = (null == refs.getHash()) ? "master" : refs.getHash();
        setTitle(owner + " / " + name);
        initSpinnerAdapter();
        doAsyncTask(owner, name, hash);
        initListView();
        initSpinner();
    }

    /**
     * スピナーアダプターの初期化
     */
    private void initSpinnerAdapter() {
        mSpinnerAdapter = new TreeAdapter(BlobsActivity.this, android.R.layout.simple_spinner_item,
                new ArrayList<KeyValuePair>());
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinnerAdapter.add(new KeyValuePair("/", 0));
    }

    /**
     * リストビューの初期化
     */
    private void initListView() {
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final KeyValuePair keyValuePair = (KeyValuePair) ((ListView) parent)
                        .getItemAtPosition(position);
                final Refs refs = (Refs) getIntent().getExtras().getSerializable(Extra.REFS);
                final String owner = refs.getOwner();
                final String name = refs.getName();
                final Intent intent = new Intent(getApplicationContext(), CodeViewActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("owner", owner);
                intent.putExtra("sha", keyValuePair.getValue());
                intent.putExtra("fileName", keyValuePair.getKey());
                startActivity(intent);
            }
        });
    }

    /**
     * 処理に失敗した場合のトースト表示
     * 
     * @param message
     */
    private void faild(final int message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                BlobsActivity.this.finish();
            }
        });
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        final Response response = executeListBlobs(parameters[0], parameters[1], parameters[2]);
        if (null == response) {
            faild(R.string.out_of_memory_error);
            return;
        }
        LogEx.d(TAG, response.resp);
        final TreeMap<String, String> treeMap = parseJson(response.resp);

        if (null == treeMap) {
            faild(R.string.could_not_get_the_results);
            return;
        }

        mTree = new Tree();
        for (Iterator<?> iterator = treeMap.keySet().iterator(); iterator.hasNext();) {
            final String key = (String) iterator.next();
            LogEx.d(TAG, key + ", " + treeMap.get(key));
            makeTree(mTree, key, treeMap.get(key), 1);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                ((Spinner) findViewById(R.id.spinner)).setAdapter(mSpinnerAdapter);
            }
        });

    }

    /**
     * Spinnerの初期化
     */
    private void initSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt(getText(R.string.tree_list));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final Spinner spinner = (Spinner) parent;
                final int level = Integer.parseInt(((KeyValuePair) spinner
                        .getItemAtPosition(position))
                        .getValue());
                final Tree tree = searchTree(level, position);
                mListView.setAdapter(new KeyValuePairAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_1, tree.getBlobList()));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0) {
            }
        });
    }

    /**
     * 目的のツリーを走査する
     * 
     * @param level
     * @param position
     * @return
     */
    private Tree searchTree(final int level, int position) {
        String key = mSpinnerAdapter.getItem(position).getKey();
        if (0 == level) {
            return mTree;
        } else {
            while (level != Integer.parseInt(mSpinnerAdapter.getItem(position).getValue())) {
                key = mSpinnerAdapter.getItem(--position).getKey();
            }
            return searchTree(level - 1, position - 1).getTree(key);
        }
    }

    /**
     * ツリーを作る
     * 
     * @param parent
     * @param key
     * @param value ハッシュ値
     * @param level 階層の深さ
     */
    private void makeTree(final Tree parent, final String key, final String value, int level) {
        if (-1 == key.indexOf("/")) {
            parent.addBlob(new KeyValuePair(key, value));
        } else {
            final String childKey = key.substring(0, key.indexOf("/"));
            Tree tree = parent.getTree(childKey);
            if (null == tree) {
                tree = new Tree();
                parent.putTree(childKey, tree);
                mSpinnerAdapter.add(new KeyValuePair(childKey, level));
            }
            makeTree(tree, key.substring(key.indexOf("/") + 1), value, ++level);
        }
    }

    /**
     * 実際にAPIを叩く
     * 
     * @param owner
     * @param name
     * @param treeSha
     * @return
     */
    private Response executeListBlobs(final String owner, final String name, final String treeSha) {
        final GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        try {
            return ghapi.object.list_blobs(owner, name, treeSha);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * JSONをパースしてMapに入れる
     * 
     * @param json
     * @return
     */
    private TreeMap<String, String> parseJson(final String json) {
        final TreeMap<String, String> treeMap = new TreeMap<String, String>();
        try {
            final JSONObject jsonObject = new JSONObject(json).getJSONObject("blobs");
            for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
                final String key = (String) iterator.next();
                treeMap.put(key, jsonObject.getString(key));
            }
        } catch (final JSONException e) {
            e.printStackTrace();
            return null;
        }
        return treeMap;
    }

    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            final BlobsMenuDialog blobsMenuDialog = new BlobsMenuDialog(this, (Refs) getIntent()
                    .getExtras().getSerializable(Extra.REFS));
            blobsMenuDialog.setOwnerActivity(this);
            blobsMenuDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
