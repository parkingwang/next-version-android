/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.download;

import com.parkingwang.version.ApkDownloader;
import com.parkingwang.version.ApkInfo;
import com.parkingwang.version.Version;
import com.parkingwang.version.support.Paths;
import com.parkingwang.version.support.Priority;

import java.io.File;

/**
 * 处理本地APK文件的下载器。即解析/读取缓存文件的下载器。
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class LocalFileApkDownloader extends ApkDownloader.AcceptedApkDownloader {

    @Override
    protected boolean accept(Version version) {
        // 本地APK文件下载器，只处理Version返回的URI路径为 / 开头的本地路径。
        return version.isLocalUri();
    }

    @Override
    protected ApkInfo download(Version version) {
        // 直接从本地缓存文件中解析出APK信息
        return ApkInfo.ofFile(new File(Paths.resolvePublicPath(version.getFileName())));
    }

    @Override
    public int priority() {
        return Priority.HIGH_H2;
    }
}
