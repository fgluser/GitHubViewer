package net.flaxia.android.githubviewer.model;

import org.json.JSONObject;

public class User extends Base {
    private static final long serialVersionUID = 5636317037796900464L;
    public static final String USERNAME = "username";

    public User(JSONObject json) {
        super(json);
    }
}
