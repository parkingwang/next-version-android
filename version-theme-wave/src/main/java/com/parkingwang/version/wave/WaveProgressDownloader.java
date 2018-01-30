/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.wave;

import android.content.Context;

import com.parkingwang.version.Version;
import com.parkingwang.version.download.NetworkApkDownloader;
import com.parkingwang.version.support.OnDownloadProgressNotifier;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class WaveProgressDownloader extends NetworkApkDownloader {

    public static WaveProgressDownloader create(Context context){
        return new WaveProgressDownloader(context);
    }

    public WaveProgressDownloader(final Context context) {
        super(new OnDownloadProgressNotifier() {

            @Override
            public void onStart(Version targetVersion) {
                // 下载时，需要显示进度条Activity
                ProgressDialogActivity.show(context);
            }

            @Override
            protected void onProgress(long totalBytes, int progress, boolean isFinished) {
                ProgressDialogActivity.updateProgress(context, totalBytes, progress, isFinished);
            }

            @Override
            public void onCompleted() {
                ProgressDialogActivity.hide(context);
            }
        });
    }

}
