package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoadingDialog extends ProgressDialog {
    protected Activity mActivity;

    public LoadingDialog(Activity activity) {
        this(activity, R.string.now_loading);
    }
    
    public LoadingDialog(Activity activity, int resId){
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
