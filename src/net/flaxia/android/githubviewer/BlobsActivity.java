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
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blobs);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("/");
        setup();
    }

    private void setup() {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        spinner.setAdapter(adapter);
    }

    private void makeTree(Tree parent, String key, String value, int level) {
        if (-1 == key.indexOf("/")) {
            parent.putBlob(key, value);
        } else {
            String childKey = key.substring(0, key.indexOf("/") + 1);
            Tree tree = parent.getTree(childKey);
            if (null == tree) {
                tree = new Tree();
                parent.putTree(childKey, tree);
                adapter.add(CommonHelper.multiply("*", level) + childKey);
            }
            makeTree(tree, key.substring(key.indexOf("/") + 1), value, ++level);
        }
    }

    private Response executeListBlobs(String owner, String name, String treeSha) {
        GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        return ghapi.object.list_blobs(owner, name, "master");
    }

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
