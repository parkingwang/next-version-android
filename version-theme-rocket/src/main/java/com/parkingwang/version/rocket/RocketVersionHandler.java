/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.rocket;

import android.support.v4.app.FragmentActivity;

import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionFoundHandler;
import com.parkingwang.version.support.ContextX;
import com.parkingwang.version.support.Priority;
import com.parkingwang.version.support.VersionBlockingFragment;


/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class RocketVersionHandler extends ContextX implements VersionFoundHandler {

    public static RocketVersionHandler create(FragmentActivity context){
        return new RocketVersionHandler(context);
    }

    private final FragmentActivity mActivity;

    public RocketVersionHandler(FragmentActivity context) {
        super(context);
        mActivity = context;
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }

    @Override
    public boolean handle(final NextVersion engine, Version version) {
        // 手动触发或非强制下载安装级别，可以弹出提示窗口：
        if (!Version.UpgradeLevel.FORCE_INSTALL.equals(version.upgradeLevel)){
            VersionDialogFragment.newInstance(version,
                    // 强制级别不可取消
                    !Version.UpgradeLevel.FORCE_EACH.equals(version.upgradeLevel)
                            && !Version.UpgradeLevel.FORCE_INSTALL.equals(version.upgradeLevel))
                    .show(mActivity, new VersionBlockingFragment.OnUpgradeClickListener() {
                        @Override
                        public void onClick(Version version) {
                            engine.upgrade(version);
                        }
                    });
            return true;
        }else{
            return false;
        }
    }



}
