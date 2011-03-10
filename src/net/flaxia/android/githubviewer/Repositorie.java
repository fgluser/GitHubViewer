package net.flaxia.android.githubviewer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class Repositorie implements Serializable {
    private static final long serialVersionUID = 5636317037796900464L;
    private HashMap<String, String> mHashMap;

    public Repositorie(JSONObject json) {
        mHashMap = new HashMap<String, String>();
        for (Iterator<?> iterator = json.keys(); iterator.hasNext();) {
            String key = (String) iterator.next();
            try {
                mHashMap.put(key, json.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public String get(String key) {
        return mHashMap.get(key);
    }
}
