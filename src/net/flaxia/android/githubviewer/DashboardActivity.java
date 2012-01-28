
package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

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
}
