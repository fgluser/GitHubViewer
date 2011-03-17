package net.flaxia.android.githubviewer.util;

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

    public static String multiply(String str, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static int continuousCount(String str, char c) {
        int level = 0;
        while (str.charAt(level) == '*') {
            level++;
        }
        return level;
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

    public static String ucFirst(String str) {
        return (null == str) ? null : str.substring(0, 1).toUpperCase()
                + str.substring(1).toLowerCase();
    }
    
    public static String getLanguageName(String suffix){
        String languageName = "Plain";
        if(suffix.equals("py")){
            languageName = "Python";
        }else if(suffix.equals("java")){
            languageName = "Java";
        }
        
        return languageName;
    }
}
