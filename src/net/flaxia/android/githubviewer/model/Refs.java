
package net.flaxia.android.githubviewer.model;

import java.io.Serializable;

public class Refs implements Serializable {
    private static final long serialVersionUID = 1217417202588331761L;
    private String mOwner;
    private String mName;
    private String mKey;
    private String mHash;

    public Refs(final String owner, final String name, final String key, final String hash) {
        mOwner = owner;
        mName = name;
        mKey = key;
        mHash = hash;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(final String owner) {
        mOwner = owner;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(final String key) {
        mKey = key;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(final String hash) {
        mHash = hash;
    }
}
