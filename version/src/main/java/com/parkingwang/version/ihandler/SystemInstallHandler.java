/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.ihandler;

import android.content.Context;
import android.os.Process;

import com.parkingwang.version.ApkInfo;
import com.parkingwang.version.AppLogger;
import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionInstallHandler;
import com.parkingwang.version.support.ContextX;
import com.parkingwang.version.support.Priority;
import com.parkingwang.version.support.SystemApkInstaller;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class SystemInstallHandler extends ContextX implements VersionInstallHandler {

    public SystemInstallHandler(Context context) {
        super(context);
    }

    @Override
    public int priority() {
        return Priority.LOW_L1;
    }

    @Override
    final public boolean handle(NextVersion engine, Version version, ApkInfo apkInfo) {
        SystemApkInstaller.install(getContext(), apkInfo);
        if (version.upgradeLevel == Version.UpgradeLevel.FORCE_EACH
                || version.upgradeLevel == Version.UpgradeLevel.FORCE_INSTALL) {
            AppLogger.d("强制更新，退出应用");
            Process.killProcess(Process.myPid());
        }
        return true;
    }

}
