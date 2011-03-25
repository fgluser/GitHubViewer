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

    public Bookmark(long id, String owner, String name, String tree, String hash, String note) {
        mId = id;
        mOwner = owner;
        mName = name;
        mTree = tree;
        mHash = hash;
        mNote = note;
    }

    public Bookmark(String owner, String name, String tree, String hash, String note) {
        this(0, owner, name, tree, hash, note);
    }

    public long getId() {
        return mId;
    }

    public void setId(long Id) {
        mId = Id;
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

    public String getTree() {
        return mTree;
    }

    public void setTree(String tree) {
        mTree = tree;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(String hash) {
        mHash = hash;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

}
