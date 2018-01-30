/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.support;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.parkingwang.version.ApkInfo;

import java.io.File;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class SystemApkInstaller {

    public static void install(Context context, ApkInfo info){
        final Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(new File(info.path)), ApkInfo.APK_MIME);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }
}
