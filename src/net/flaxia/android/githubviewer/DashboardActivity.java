
package net.flaxia.android.githubviewer;

import java.io.File;

import net.flaxia.android.githubviewer.util.Configuration;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class DashboardActivity extends Activity {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_dashbord);
    }

    public void onUserSearchButton(final View view) {
        startActivity(new Intent(getApplicationContext(), UserSearchActivity.class));
    }

    public void onInformationButton(final View view) {
        startActivity(new Intent(getApplicationContext(), InformationActivity.class));
    }

    public void onConfigureButton(final View view) {
        startActivity(new Intent(getApplicationContext(), ConfigureActivity.class));
    }

    public void onLocalDriveButton(final View view) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        final File targetDir = new File(prefs.getString(ConfigureActivity.SAVE_DIR,
                Configuration.DEFAULT_SAVE_PATH));
        targetDir.mkdirs();
        if (!targetDir.canRead()) {
            Toast.makeText(getApplicationContext(), R.string.could_not_load_external_memory,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(getApplicationContext(), LocalExplorerActivity.class).putExtra(
                Extra.EXPLORER_PATH, targetDir.getAbsolutePath()));
    }
}
