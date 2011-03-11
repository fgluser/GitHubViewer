package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().isDebuggable = isDebuggable();
        startActivity(new Intent(getApplicationContext(), DashbordActivity.class));
    }

    private boolean isDebuggable() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            return ((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE);
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
