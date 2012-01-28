
package net.flaxia.android.githubviewer;

import android.view.View;

abstract class BaseMenuActivity extends BaseActivity {

    @Override
    protected int getTitleBarLayoutId() {
        return R.layout.title_bar_menu;
    }

    public void onMenuButton(final View view) {
        openOptionsMenu();
    }
}
