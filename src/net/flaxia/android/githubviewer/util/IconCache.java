package net.flaxia.android.githubviewer.util;

import java.util.HashMap;

import android.graphics.drawable.Drawable;

public class IconCache implements android.text.Html.ImageGetter {
    private static IconCache mInstance;
    private HashMap<String, Drawable> mDrawableHashMap;

    private IconCache() {
        mDrawableHashMap = new HashMap<String, Drawable>();
    }

    public static synchronized IconCache getInstance() {
        if (null == mInstance) {
            mInstance = new IconCache();
        }
        return mInstance;
    }

    @Override
    public Drawable getDrawable(String source) {
        Drawable drawable = mDrawableHashMap.get(source);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        return drawable;
    }

    public void putDrawable(String key, Drawable drawable) {
        mDrawableHashMap.put(key, drawable);
    }
}
