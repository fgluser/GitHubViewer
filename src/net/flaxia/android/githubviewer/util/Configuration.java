package net.flaxia.android.githubviewer.util;

public class Configuration {
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
