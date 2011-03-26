package net.flaxia.android.githubviewer;

import java.io.File;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Configuration;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DashboardActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);
    }

    /**
     * 検索ボタンを押したときの処理
     * 
     * @param view
     */
    public void onSearchButton(View view) {
        Context context = getApplicationContext();
        String q = ((EditText) findViewById(R.id.q)).getText().toString();
        if (CommonHelper.isEmpty(q)) {
            Toast.makeText(context, R.string.search_word_is_empty, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(Extra.Q, q);
        startActivity(intent);
    }

    public void onInformationButton(View view) {
        startActivity(new Intent(getApplicationContext(), InformationActivity.class));
    }

    public void onBookmarkButton(View view) {
        startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
    }

    public void onConfigureButton(View view) {
        startActivity(new Intent(getApplicationContext(), ConfigureActivity.class));
    }

    public void onLocalDriveButton(View view) {
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
