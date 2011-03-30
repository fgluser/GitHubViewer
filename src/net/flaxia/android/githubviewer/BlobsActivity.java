package net.flaxia.android.githubviewer;

import java.util.Iterator;
import java.util.TreeMap;

import net.flaxia.android.githubviewer.adapter.KeyValuePairAdapter;
import net.flaxia.android.githubviewer.adapter.TreeAdapter;
import net.flaxia.android.githubviewer.model.KeyValuePair;
import net.flaxia.android.githubviewer.model.Refs;
import net.flaxia.android.githubviewer.model.Tree;
import net.flaxia.android.githubviewer.util.Extra;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
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
    private static final String ADAPTER = "adapter";
    private static final String TREE = "tree";
    private TreeAdapter mSpinnerAdapter;
    private Tree mTree;
    private ListView mListView;
    private Refs mRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blobs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mRefs) {
            mRefs = (Refs) getIntent().getExtras().getSerializable(Extra.REFS);
        }
        String owner = mRefs.getOwner();
        String name = mRefs.getName();
        setTitle(owner + " / " + name);
        if (null == mSpinnerAdapter) {
            initSpinnerAdapter();
            mSpinnerAdapter.add(new KeyValuePair("/", 0));
            String hash = (null == mRefs.getHash()) ? "master" : mRefs.getHash();
            doAsyncTask(owner, name, hash);
        } else {
            ((Spinner) findViewById(R.id.spinner)).setAdapter(mSpinnerAdapter);
        }
        initListView();
        initSpinner();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Extra.REFS, mRefs);
        outState.putSerializable(TREE, mTree);
        outState.putSerializable(ADAPTER, mSpinnerAdapter.getList().toArray(new KeyValuePair[0]));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRefs = (Refs) savedInstanceState.getSerializable(Extra.REFS);
        mTree = (Tree) savedInstanceState.getSerializable(TREE);
        initSpinnerAdapter();
        mSpinnerAdapter.setList((KeyValuePair[]) savedInstanceState.getSerializable(ADAPTER));
    }

    /**
     * スピナーアダプターの初期化
     */
    private void initSpinnerAdapter() {
        mSpinnerAdapter = new TreeAdapter(BlobsActivity.this, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    }

    /**
     * リストビューの初期化
     */
    private void initListView() {
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KeyValuePair keyValuePair = (KeyValuePair) ((ListView) parent)
                        .getItemAtPosition(position);
                String owner = mRefs.getOwner();
                String name = mRefs.getName();
                Intent intent = new Intent(getApplicationContext(), CodeViewActivity.class);
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
                mLoadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                BlobsActivity.this.finish();
            }
        });
    }

    @Override
    protected void executeAsyncTask(final String... parameters) {
        Response response = executeListBlobs(parameters[0], parameters[1], parameters[2]);
        if (null == response) {
            faild(R.string.out_of_memory_error);
            return;
        }
        LogEx.d(TAG, response.resp);
        TreeMap<String, String> treeMap = parseJson(response.resp);

        if (null == treeMap) {
            faild(R.string.could_not_get_the_results);
            return;
        }

        mTree = new Tree();
        for (Iterator<?> iterator = treeMap.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            LogEx.d(TAG, key + ", " + treeMap.get(key));
            makeTree(mTree, key, treeMap.get(key), 1);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
                ((Spinner) findViewById(R.id.spinner)).setAdapter(mSpinnerAdapter);
            }
        });

    }

    /**
     * Spinnerの初期化
     */
    private void initSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt(getText(R.string.tree_list));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                int level = Integer.parseInt(((KeyValuePair) spinner.getItemAtPosition(position))
                        .getValue());
                Tree tree = searchTree(level, position);
                mListView.setAdapter(new KeyValuePairAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_1, tree.getBlobList()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
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
    private Tree searchTree(int level, int position) {
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
     * @param value
     *            ハッシュ値
     * @param level
     *            階層の深さ
     */
    private void makeTree(Tree parent, String key, String value, int level) {
        if (-1 == key.indexOf("/")) {
            parent.addBlob(new KeyValuePair(key, value));
        } else {
            String childKey = key.substring(0, key.indexOf("/"));
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
    private Response executeListBlobs(String owner, String name, String treeSha) {
        GitHubAPI ghapi = new GitHubAPI();
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
    private TreeMap<String, String> parseJson(String json) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        try {
            JSONObject jsonObject = new JSONObject(json).getJSONObject("blobs");
            for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
                String key = (String) iterator.next();
                treeMap.put(key, jsonObject.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return treeMap;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            BlobsMenuDialog blobsMenuDialog = new BlobsMenuDialog(this, (Refs) getIntent()
                    .getExtras().getSerializable(Extra.REFS));
            blobsMenuDialog.setOwnerActivity(this);
            blobsMenuDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
