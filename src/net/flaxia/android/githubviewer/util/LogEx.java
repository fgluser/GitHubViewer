
package net.flaxia.android.githubviewer.util;

public class LogEx {
    public static void d(final String tag, final String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(final String tag, final int msg) {
        d(tag, String.valueOf(msg));
    }

    public static void e(final String tag, final String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(final String tag, final int msg) {
        e(tag, String.valueOf(msg));
    }

    public static void i(final String tag, final String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(final String tag, final int msg) {
        i(tag, String.valueOf(msg));
    }

    public static void v(final String tag, final String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(final String tag, final int msg) {
        v(tag, String.valueOf(msg));
    }

    public static void w(final String tag, final String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(final String tag, final int msg) {
        w(tag, String.valueOf(msg));
    }
}
