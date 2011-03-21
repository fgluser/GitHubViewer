package net.flaxia.android.githubviewer.model;

import java.io.Serializable;

public class Refs implements Serializable {
    private static final long serialVersionUID = 1217417202588331761L;
    private String mOwner;
    private String mName;
    private String mKey;
    private String mHash;

    public Refs(String owner, String name, String key, String hash) {
        mOwner = owner;
        mName = name;
        mKey = key;
        mHash = hash;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(String hash) {
        mHash = hash;
    }
}
