package net.flaxia.android.githubviewer;

import java.util.Iterator;
import java.util.TreeMap;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BlobsActivity extends Activity {
    public static final String REPOSITORIE = "repositorie";
    private static final String TAG = BlobsActivity.class.getSimpleName();
    private Repositorie mRepositorie;
    ArrayAdapter<String> mSpinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blobs);
        mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.add("/");
        setup();
    }

    /**
     * Activityの初期化
     */
    private void setup() {
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRepositorie = (Repositorie) getIntent().getExtras().getSerializable(REPOSITORIE);
        Response response = executeListBlobs(mRepositorie.get("owner"), mRepositorie.get("name"),
                "master");
        LogEx.d(TAG, response.resp);
        TreeMap<String, String> treeMap = parseJson(response.resp);

        Tree tree = new Tree();

        for (Iterator<?> iterator = treeMap.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            LogEx.d(TAG, key + ", " + treeMap.get(key));
            makeTree(tree, key, treeMap.get(key), 1);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(mSpinnerAdapter);
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
            parent.putBlob(key, value);
        } else {
            String childKey = key.substring(0, key.indexOf("/") + 1);
            Tree tree = parent.getTree(childKey);
            if (null == tree) {
                tree = new Tree();
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
