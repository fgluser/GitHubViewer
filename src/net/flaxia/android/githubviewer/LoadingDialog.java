package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoadingDialog extends ProgressDialog {
    protected Activity mActivity;

    public LoadingDialog(Activity activity) {
        super(activity);
        mActivity = activity;
        
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
