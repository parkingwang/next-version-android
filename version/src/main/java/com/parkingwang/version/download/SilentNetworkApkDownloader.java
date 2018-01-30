/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.download;

import com.parkingwang.version.AppLogger;
import com.parkingwang.version.Version;
import com.parkingwang.version.support.Priority;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class SilentNetworkApkDownloader extends NetworkApkDownloader {

    private static final String TAG = "SilentDownloader";

    public SilentNetworkApkDownloader() {
        super(new OnDownloadProgressListener() {
            @Override
            public void onStart(Version targetVersion) {
                AppLogger.d("开始下载新版本APK文件");
            }

            @Override
            public void onUpdate(long totalBytes, long currentBytes, boolean done) {
                // nop
            }

            @Override
            public void onCompleted() {
                AppLogger.d("新版本APK文件下载完成");
            }
        });
    }

    @Override
    public int priority() {
        return Priority.LOW_L2;
    }
}
