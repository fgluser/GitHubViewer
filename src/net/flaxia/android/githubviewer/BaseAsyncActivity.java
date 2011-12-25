
package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

public abstract class BaseAsyncActivity extends Activity {
    protected ProgressDialog mProgressDialog;
    protected Handler mHandler;

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
    }

    /**
     * 別スレッドで通信を行う
     */
    protected void doAsyncTask(final String... parameters) {
        asyncReady();
        new Thread(new Runnable() {
            public void run() {
                executeAsyncTask(parameters);
            }
        }).start();
    }

    /**
     * 実際に別スレッドで行う処理を書く
     * 
     * @param parameters
     */
    abstract protected void executeAsyncTask(final String... parameters);

    /**
     * 別スレッドで処理を行う前処理
     */
    protected void asyncReady() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.now_loading), true,
                true);
    }

    /**
     * 画面回転対策
     */
    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    /**
     * ダイアログが有効化を確認してから閉じる
     */
    protected void dismissDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
