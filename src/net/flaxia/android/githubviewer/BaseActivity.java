
package net.flaxia.android.githubviewer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

abstract class BaseActivity extends FragmentActivity {
    private TextView mTitle;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mTitle) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
            mTitle = (TextView) findViewById(R.id.title);
            setTitle(getTitle());
        }
    }

    @Override
    public void setTitle(final CharSequence title) {
        super.setTitle(title);
        mTitle.setText(title);
    }

    public void onMenuButton(final View view) {
        openOptionsMenu();
    }

}
