
package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoadingDialog extends ProgressDialog {
    protected final Activity mActivity;

    public LoadingDialog(final Activity activity) {
        this(activity, R.string.now_loading);
    }

    public LoadingDialog(final Activity activity, final int resId) {
        super(activity);
        mActivity = activity;

        setMessage(activity.getString(resId));
        setIndeterminate(false);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(true);
        show();
    }

    @Override
    public void cancel() {
        super.cancel();
        mActivity.finish();
    }

}
