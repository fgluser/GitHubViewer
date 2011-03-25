package net.flaxia.android.githubviewer.util;

import android.os.Environment;

public class Configuration {
    public static final String DEFAULT_SAVE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath()
            + "/GitHubViewer";
    private static Configuration mInstance;
    public boolean isDebuggable;

    private Configuration() {
    }

    public static synchronized Configuration getInstance() {
        if (null == mInstance) {
            mInstance = new Configuration();
        }
        return mInstance;
    }
}
