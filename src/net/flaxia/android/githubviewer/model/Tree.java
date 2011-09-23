
package net.flaxia.android.githubviewer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class Tree {
    private final TreeMap<String, Tree> mChildTree;
    private final ArrayList<KeyValuePair> mChildBlob;

    public Tree() {
        mChildTree = new TreeMap<String, Tree>();
        mChildBlob = new ArrayList<KeyValuePair>();
    }

    public Iterator<String> getTreeIterator() {
        return mChildTree.keySet().iterator();
    }

    public void addBlob(final KeyValuePair kvp) {
        mChildBlob.add(kvp);
    }

    public Tree getTree(final String key) {
        return mChildTree.get(key);
    }

    public void putTree(final String key, final Tree value) {
        mChildTree.put(key, value);
    }

    public ArrayList<KeyValuePair> getBlobList() {
        return mChildBlob;
    }
}
