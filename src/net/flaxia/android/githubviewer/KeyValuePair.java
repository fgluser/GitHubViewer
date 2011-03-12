package net.flaxia.android.githubviewer;

public class KeyValuePair {
    private String mKey;
    private String mValue;

    public KeyValuePair(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }
}
