package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.util.IconCache;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
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
        Resources resources = getResources();
        iconCache.putDrawable("dir_blank", resources.getDrawable(R.drawable.dir_blank));
        iconCache.putDrawable("dir_i", resources.getDrawable(R.drawable.dir_i));
        iconCache.putDrawable("dir_l", resources.getDrawable(R.drawable.dir_l));
        iconCache.putDrawable("dir_t", resources.getDrawable(R.drawable.dir_t));
        iconCache.putDrawable("dir", resources.getDrawable(R.drawable.dir));

        iconCache.putDrawable("type_java", resources.getDrawable(R.drawable.type_java));
        iconCache.putDrawable("type_php", resources.getDrawable(R.drawable.type_php));
        iconCache.putDrawable("type_py", resources.getDrawable(R.drawable.type_py));
        iconCache.putDrawable("type_txt", resources.getDrawable(R.drawable.type_txt));
        iconCache.putDrawable("type_unknown", resources.getDrawable(R.drawable.type_unknown));
        iconCache.putDrawable("type_xml", resources.getDrawable(R.drawable.type_xml));
    }

    /**
     * AndroidManifestのデバッグの設定を返す
     * 
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
