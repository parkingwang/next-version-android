package com.parkingwang.version.support;

import com.parkingwang.version.Version;
import com.parkingwang.version.download.NetworkApkDownloader;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public abstract class OnDownloadProgressNotifier implements NetworkApkDownloader.OnDownloadProgressListener {

    public static final String ACTION_UPDATE_PROGRESS = "com.parkingwang.version:download:progress";
    public static final String ACTION_UPDATE_COMPLETED = "com.parkingwang.version:download:completed";

    private int mProgress = -1;

    @Override
    public void onStart(Version targetVersion) {
    }

    @Override
    public void onUpdate(long totalBytes, long currentBytes, boolean isFinished) {
        // 发送进度消息给下载进度Activity
        final int progress = Double.valueOf((currentBytes * 1.0 / totalBytes) * 100).intValue();
        // 通过进度管制，限制通过Broadcast发送消息的数量
        if (progress > mProgress) {
            mProgress = progress;
            onProgress(totalBytes, progress, isFinished);
        }
    }

    protected abstract void onProgress(long totalBytes, int progress, boolean isFinished);

}
