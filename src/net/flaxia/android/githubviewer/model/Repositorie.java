package net.flaxia.android.githubviewer.model;

import org.json.JSONObject;

public class Repositorie extends Base {
    private static final long serialVersionUID = 5636317037796900464L;
    public static final String NAME = "name";
    public static final String OWNER = "owner";
    public static final String HOMEPAGE = "homepage";
    public static final String DESCRIPTION = "description";
    
    public Repositorie(final JSONObject json) {
        super(json);
    }
}
