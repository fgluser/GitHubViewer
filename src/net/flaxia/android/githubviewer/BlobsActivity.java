package net.flaxia.android.githubviewer;

import java.util.Iterator;
import java.util.TreeMap;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.LogEx;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class BlobsActivity extends Activity {
    public static final String REPOSITORIE = "repositorie";
    private static final String TAG = BlobsActivity.class.getSimpleName();
    private Repositorie mRepositorie;
    ArrayAdapter<String> mSpinnerAdapter;
    private Tree mTree;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blobs);
        initListView();
        initSpinner();
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.listView);
    }

    /**
     * Spinnerの初期化
     */
    private void initSpinner() {
        mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.add("/");
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRepositorie = (Repositorie) getIntent().getExtras().getSerializable(REPOSITORIE);
        Response response = executeListBlobs(mRepositorie.get("owner"), mRepositorie.get("name"),
                "master");
        LogEx.d(TAG, response.resp);
        TreeMap<String, String> treeMap = parseJson(response.resp);

        mTree = new Tree();

        for (Iterator<?> iterator = treeMap.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            LogEx.d(TAG, key + ", " + treeMap.get(key));
            makeTree(mTree, key, treeMap.get(key), 1);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                String str = (String) spinner.getItemAtPosition(position);
                int level = CommonHelper.continuousCount(str, '*');
                LogEx.d(TAG, "lv :" + level + ", p: " + position);
                Tree tree = searchTree(level, position);
                KeyValuePair[] keyValuePairs = tree.getBlobArray();
                LogEx.d(TAG, keyValuePairs[0].getKey());
                mListView.setAdapter(new ListBlobsAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_1, tree.getBlobArray()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private Tree searchTree(int level, int position) {
        String key = mSpinnerAdapter.getItem(position);
        if (0 == level) {
            return mTree;
        } else {
            position--;
            while (level != CommonHelper.continuousCount(key, '*')) {
                key = mSpinnerAdapter.getItem(position);
                position--;
            }
            Tree tree = searchTree(level - 1, position);
            LogEx.d(TAG, "KEY: "+key.replaceFirst(CommonHelper.multiply("\\*", level), ""));
            return tree.getTree(key.replaceFirst(CommonHelper.multiply("\\*", level), ""));
        }
    }

    /**
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
            String childKey = key.substring(0, key.indexOf("/") + 1);
            Tree tree = parent.getTree(childKey);
            if (null == tree) {
                tree = new Tree();
                Log.d(TAG, "childKey: " + childKey);
                parent.putTree(childKey, tree);
                mSpinnerAdapter.add(CommonHelper.multiply("*", level) + childKey);
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
        // TODO: ブランチをmasterに固定しているのは改めたい
        return ghapi.object.list_blobs(owner, name, "master");
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
        }
        return treeMap;
    }
}