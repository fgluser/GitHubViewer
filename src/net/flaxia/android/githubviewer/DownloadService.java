
package net.flaxia.android.githubviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class DownloadService extends Service {
    public static final String SAVE_PATH = "zipPath";
    public static final String DOWNLOAD_URL = "downloadUrl";
    private static int REQUEST_CODE = 0;

    @Override
    public void onStart(final Intent intent, final int startId) {
        new Thread(new Runnable() {
            public void run() {
                final Intent notificationIntent = new Intent(getApplicationContext(),
                        StartActivity.class);
                notificationIntent.putExtra(Extra.REQUEST_CODE, REQUEST_CODE);
                notification(PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE,
                        notificationIntent, 0), R.string.start_download, R.string.downloading);
                final Bundle extras = intent.getExtras();
                final File target = new File(extras.getString(SAVE_PATH));
                target.getParentFile().mkdirs();
                try {
                    CommonHelper.download(new URL(extras.getString(DOWNLOAD_URL)), target);
                } catch (final MalformedURLException e) {
                    notification(PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                            R.string.download_failed);
                    e.printStackTrace();
                    return;
                } catch (final IOException e) {
                    notification(PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                            R.string.download_failed);
                    e.printStackTrace();
                    return;
                }
                notification(PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                        R.string.start_unzip, R.string.unzipping);
                if (!unzip(target.getAbsolutePath())) {
                    notification(PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                            R.string.unzip_failed);
                    return;
                }
                target.delete();

                notificationIntent.putExtra(Extra.ACTIVITY, Extra.LOCAL_EXPLORER_ACTIVITY);
                notificationIntent.putExtra(Extra.EXPLORER_PATH, CommonHelper
                        .removeExtension(target.getAbsolutePath())
                        + "/");
                notification(PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                        R.string.download_is_complete);
            }
        }).start();
    }

    private void notification(final PendingIntent pendingIntent, final int notificationMessage) {
        notification(pendingIntent, notificationMessage, notificationMessage);
    }

    private void notification(final PendingIntent pendingIntent, final int notificationMessage,
            final int message) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification(R.drawable.ic_launcher,
                getString(notificationMessage), System.currentTimeMillis());
        notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name),
                getString(message), pendingIntent);
        notificationManager.notify(0, notification);
    }

    private boolean unzip(final String zipPath) {
        boolean flag = true;
        final File outDir = new File(CommonHelper.removeExtension(zipPath));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try {
            final ZipFile zipFile = new ZipFile(zipPath);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final File outFile = new File(outDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdir();
                } else {
                    bin = new BufferedInputStream(zipFile.getInputStream(entry));
                    bout = new BufferedOutputStream(new FileOutputStream(outFile));
                    int bytedata = 0;
                    while ((bytedata = bin.read()) != -1) {
                        bout.write(bytedata);
                    }
                    bout.flush();
                }
            }
        } catch (final ZipException e) {
            flag = false;
            e.printStackTrace();
        } catch (final IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            try {
                if (bin != null)
                    bin.close();
                if (bout != null)
                    bout.close();
            } catch (final IOException e) {
                flag = false;
                e.printStackTrace();
            }
        }

        return flag;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
