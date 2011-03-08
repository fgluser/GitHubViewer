package net.flaxia.android.githubviewer;

public class CommonHelper {
    /**
     * stringが空か調べる
     * 
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        if (null == string || 0 == string.length() || !string.matches("\\S*")) {
            return true;
        }
        return false;
    }
}
