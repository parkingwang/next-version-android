package com.parkingwang.version.check;

import android.content.Context;

import com.parkingwang.version.Version;
import com.parkingwang.version.download.NetworkApkDownloader;


/**
 * @author 占迎辉 (zhanyinghui@parkingwang.com)
 * @version 2017/10/23
 */

public class CheckVersionDownloader extends NetworkApkDownloader {
    public static CheckVersionDownloader create(Context context) {
        return new CheckVersionDownloader(context);
    }

    public CheckVersionDownloader(final Context context) {
        super(new OnDownloadProgressListener() {

            private int mProgress = -1;

            @Override
            public void onStart(Version targetVersion) {
            }

            @Override
            public void onUpdate(long totalBytes, long currentBytes, boolean isFinished) {
                // 发送进度消息给下载进度Activity
                int progress = Double.valueOf((currentBytes * 1.0 / totalBytes) * 100).intValue();
                // 通过进度管制，限制通过Broadcast发送消息的数量
                if (progress > mProgress) {
                    VersionDialogFragment.updateProgress(context, totalBytes, progress, isFinished);
                    mProgress = progress;
                }
            }

            @Override
            public void onCompleted() {
                VersionDialogFragment.updateCompleted(context);
            }
        });
    }
}
