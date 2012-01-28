
package net.flaxia.android.githubviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class InformationActivity extends BaseActivity {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
    }

    public void onAl(final View view) {
        final Intent intent = new Intent(getBaseContext(), CodeViewActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, "file:///android_asset/al.txt");
        startActivity(intent);
    }

    public void onCc(final View view) {
        final Uri uri = Uri.parse("http://creativecommons.org/licenses/by/3.0/legalcode");
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
