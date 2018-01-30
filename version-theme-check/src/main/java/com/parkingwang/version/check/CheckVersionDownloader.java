package com.parkingwang.version.check;

import android.content.Context;

import com.parkingwang.version.download.NetworkApkDownloader;
import com.parkingwang.version.support.OnDownloadProgressNotifier;

/**
 * @author 占迎辉 (zhanyinghui@parkingwang.com)
 * @version 2017/10/23
 */

public class CheckVersionDownloader extends NetworkApkDownloader {

    public static CheckVersionDownloader create(Context context) {
        return new CheckVersionDownloader(context);
    }

    public CheckVersionDownloader(final Context context) {
        super(new OnDownloadProgressNotifier() {
            @Override
            protected void onProgress(long totalBytes, int progress, boolean isFinished) {
                VersionDialogFragment.updateProgress(context, totalBytes, progress, isFinished);
            }

            @Override
            public void onCompleted() {
                VersionDialogFragment.updateCompleted(context);
            }
        });
    }
}
