package net.flaxia.android.githubviewer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class ConfigureActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    public static final String SAVE_DIR = "saveDir";
    public static final String CODE_THEME = "codeTheme";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configure);
        ((ListPreference) findPreference(CODE_THEME)).setSummary(getPreferenceScreen()
                .getSharedPreferences().getString(CODE_THEME, "default"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(CODE_THEME)) {
            ((ListPreference) findPreference(CODE_THEME)).setSummary(sharedPreferences.getString(
                    key, "default"));
        }
    }
}
