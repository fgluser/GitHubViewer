package net.flaxia.android.githubviewer;

import java.util.Iterator;
import java.util.TreeMap;

public class Tree {
    private TreeMap<String, Tree> mChildTree;
    private TreeMap<String, String> mChildBlob;

    public Tree() {
        mChildTree = new TreeMap<String, Tree>();
        mChildBlob = new TreeMap<String, String>();
    }

    public Iterator<String> getBlobIterator() {
        return mChildBlob.keySet().iterator();
    }

    public Iterator<String> getTreeIterator() {
        return mChildTree.keySet().iterator();
    }

    public String getBlob(String key) {
        return mChildBlob.get(key);
    }

    public void putBlob(String key, String value) {
        mChildBlob.put(key, value);
    }

    public Tree getTree(String key) {
        return mChildTree.get(key);
    }

    public void putTree(String key, Tree value) {
        mChildTree.put(key, value);
    }
}
