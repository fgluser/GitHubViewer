package net.flaxia.android.githubviewer.util;

import net.flaxia.android.githubviewer.Configuration;

public class LogEx {
    public static void d(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.d(tag, msg);
        }
    }
    
    public static void d(String tag, int msg) {
        d(tag, String.valueOf(msg));
    }

    public static void e(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.e(tag, msg);
        }
    }
    
    public static void e(String tag, int msg) {
        e(tag, String.valueOf(msg));
    }

    public static void i(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.i(tag, msg);
        }
    }
    
    public static void i(String tag, int msg) {
        i(tag, String.valueOf(msg));
    }

    public static void v(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.v(tag, msg);
        }
    }
    
    public static void v(String tag, int msg) {
        v(tag, String.valueOf(msg));
    }

    public static void w(String tag, String msg) {
        if (Configuration.getInstance().isDebuggable) {
            android.util.Log.w(tag, msg);
        }
    }
    
    public static void w(String tag, int msg) {
        w(tag, String.valueOf(msg));
    }
}
