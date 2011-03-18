package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.os.Handler;

public abstract class BaseAsyncActivity extends Activity {
    protected LoadingDialog mLoadingDialog;
    protected Handler mHandler;

    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
    }

    /**
     * 非同期で通信を行う
     */
    protected void doAsyncTask(final String... parameters) {
        asyncReady();
        new Thread(new Runnable() {
            public void run() {
                executeAsyncTask(parameters);
            }
        }).start();
    }

    protected void executeAsyncTask(final String... parameters) {
        // // 非同期で行う処理
        // mHandler.post(new Runnable() {
        // @Override
        // public void run() {
        // // UIに影響する処理
        // }
        // });
    }

    protected void asyncReady() {
        mLoadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    protected void dismissDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
