package net.flaxia.android.githubviewer;

import java.util.Iterator;
import java.util.TreeMap;

import org.idlesoft.libraries.ghapi.GitHubAPI;
import org.idlesoft.libraries.ghapi.APIAbstract.Response;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

public class BlobsActivity extends Activity {
    public static final String REPOSITORIE = "repositorie";
    private static final String TAG = BlobsActivity.class.getSimpleName();
    private Repositorie mRepositorie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blobs);
        setup();
    }

    private void setup() {
        mRepositorie = (Repositorie) getIntent().getExtras().getSerializable(REPOSITORIE);
        Response response = executeListBlobs(mRepositorie.get("owner"), mRepositorie.get("name"),
                "master");
        LogEx.d(TAG, response.resp);
        TreeMap<String, String> treeMap = parseJson(response.resp);

        for (Iterator<?> iterator = treeMap.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            LogEx.d(TAG, key + ", "+ treeMap.get(key));
        }
    }

    private Response executeListBlobs(String owner, String name, String treeSha) {
        GitHubAPI ghapi = new GitHubAPI();
        ghapi.goStealth();
        return ghapi.object.list_blobs(owner, name, "master");
    }

    private TreeMap<String, String> parseJson(String json) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        LogEx.d(TAG, json);
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
