/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public interface ApkDownloader {

    int priority();

    ApkInfo applyDownload(Version version);

     abstract class AcceptedApkDownloader implements ApkDownloader {

        @Override
        final public ApkInfo applyDownload(Version version) {
            return accept(version) ? download(version) : ApkInfo.failed();
        }

        protected abstract boolean accept(Version version);

        protected abstract ApkInfo download(Version version);
    }
}
