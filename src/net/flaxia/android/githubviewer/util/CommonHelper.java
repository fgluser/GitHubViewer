package net.flaxia.android.githubviewer.util;

import java.util.Calendar;

public class CommonHelper {
    /**
     * stringが空か調べる
     * 
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        if (null == string || !string.matches(".*\\S+.*")) {
            return true;
        }
        return false;
    }

    /**
     * 拡張子を返す
     * 
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        if (null == fileName || 0 == fileName.length())
            return null;

        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return fileName;
    }

    public static String escapeSign(String source) {
        return source.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    public static String getNow() {
        final Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + 1 + "/"
                + calendar.get(Calendar.DAY_OF_MONTH) + "/" + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND);
    }
}
