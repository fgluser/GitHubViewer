package net.flaxia.android.githubviewer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;


public class Tree {
    private TreeMap<String, Tree> mChildTree;
    private ArrayList<KeyValuePair> mChildBlob;

    public Tree() {
        mChildTree = new TreeMap<String, Tree>();
        mChildBlob = new ArrayList<KeyValuePair>();
    }

    public Iterator<String> getTreeIterator() {
        return mChildTree.keySet().iterator();
    }

    public void addBlob(KeyValuePair kvp) {
        mChildBlob.add(kvp);
    }

    public Tree getTree(String key) {
        return mChildTree.get(key);
    }

    public void putTree(String key, Tree value) {
        mChildTree.put(key, value);
    }

    public ArrayList<KeyValuePair> getBlobList() {
        return mChildBlob;
    }
}
