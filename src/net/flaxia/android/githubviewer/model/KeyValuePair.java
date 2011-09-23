
package net.flaxia.android.githubviewer.model;

public class KeyValuePair {
    private String mKey;
    private String mValue;

    public KeyValuePair(final String key, final String value) {
        setKey(key);
        setValue(value);
    }

    public KeyValuePair(final String key, final int value) {
        this(key, String.valueOf(value));
    }

    public void setKey(final String key) {
        mKey = key;
    }

    public void setValue(final String value) {
        mValue = value;
    }

    public void setValue(final int value) {
        setValue(String.valueOf(value));
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }
}
