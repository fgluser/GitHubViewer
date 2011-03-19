package net.flaxia.android.githubviewer.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class Repositorie implements Serializable {
    private static final long serialVersionUID = 5636317037796900464L;
    public static final String NAME = "name";
    public static final String OWNER = "owner";
    public static final String HOMEPAGE = "homepage";
    public static final String DESCRIPTION = "description";
    
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
