package net.flaxia.android.githubviewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ConfigureActivity extends PreferenceActivity {
    public static final String SAVE_DIR = "saveDir";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configure);
    }
}
