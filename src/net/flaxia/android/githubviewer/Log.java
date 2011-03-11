package net.flaxia.android.githubviewer;

public class Log {
    public static void d(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.w(tag, msg);
        }
    }
}
