/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public interface VersionInstallHandler {

    int priority();

    /**
     * @param version 版本信息
     * @param apkInfo APK信息
     * @return 返回 True 表示当前接口已处理APK安装，后续接口将被中断。否则返回 False。
     */
    boolean handle(NextVersion engine, Version version, ApkInfo apkInfo);
}
