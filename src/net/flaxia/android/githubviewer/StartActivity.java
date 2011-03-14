package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.util.IconCache;
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
        initIcons();
        startActivity(new Intent(getApplicationContext(), DashbordActivity.class));
        finish();
    }

    /**
     * 別クラスがリソースにアクセスできるクラスとは限らないめ，ここで予め読み込んでおく
     */
    private void initIcons() {
        IconCache iconCache = IconCache.getInstance();
        iconCache.putDrawable("dir_blank", getResources().getDrawable(R.drawable.dir_blank));
        iconCache.putDrawable("dir_i", getResources().getDrawable(R.drawable.dir_i));
        iconCache.putDrawable("dir_l", getResources().getDrawable(R.drawable.dir_l));
        iconCache.putDrawable("dir_t", getResources().getDrawable(R.drawable.dir_t));
    }

    /**
     * AndroidManifestのデバッグの設定を返す
     * @return
     */
    private boolean isDebuggable() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            return ((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE);
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
