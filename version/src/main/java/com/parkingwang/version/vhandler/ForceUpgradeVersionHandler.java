/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.vhandler;

import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionFoundHandler;
import com.parkingwang.version.support.Priority;

/**
 * 强制更新App版本处理器。
 * 当接收到的App更新级别信息为3（强制更新）级别时
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class ForceUpgradeVersionHandler implements VersionFoundHandler {

    @Override
    public int priority() {
        return Priority.HIGH_H2;
    }

    @Override
    public boolean handle(NextVersion engine, Version version) {
        if (Version.UpgradeLevel.FORCE_INSTALL.equals(version.upgradeLevel)){
            engine.upgrade(version);
            return true;
        }else{
            return false;
        }
    }
}
