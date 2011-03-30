package net.flaxia.android.githubviewer.model;

import java.io.Serializable;


public class KeyValuePair implements Serializable{
    private static final long serialVersionUID = 7670948433091747935L;
    private String mKey;
    private String mValue;

    public KeyValuePair(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public KeyValuePair(String key, int value) {
        this(key, String.valueOf(value));
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void setValue(int value) {
        setValue(String.valueOf(value));
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }
}
