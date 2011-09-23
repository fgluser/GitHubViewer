
package net.flaxia.android.githubviewer.model;

import java.io.Serializable;

public class Bookmark implements Serializable {
    private static final long serialVersionUID = 1426970635477281420L;
    private long mId;
    private String mOwner;
    private String mName;
    private String mTree;
    private String mHash;
    private String mNote;

    public Bookmark(final long id, final String owner, final String name, final String tree,
            final String hash, final String note) {
        mId = id;
        mOwner = owner;
        mName = name;
        mTree = tree;
        mHash = hash;
        mNote = note;
    }

    public Bookmark(final String owner, final String name, final String tree, final String hash,
            final String note) {
        this(0, owner, name, tree, hash, note);
    }

    public long getId() {
        return mId;
    }

    public void setId(final long Id) {
        mId = Id;
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

    public String getTree() {
        return mTree;
    }

    public void setTree(final String tree) {
        mTree = tree;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(final String hash) {
        mHash = hash;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(final String note) {
        mNote = note;
    }

}
