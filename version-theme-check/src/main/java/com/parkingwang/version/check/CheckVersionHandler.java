package com.parkingwang.version.check;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionFoundHandler;
import com.parkingwang.version.support.ContextX;
import com.parkingwang.version.support.Priority;

/**
 * @author 占迎辉 (zhanyinghui@parkingwang.com)
 * @version 2017/10/16
 */

public class CheckVersionHandler extends ContextX implements VersionFoundHandler {
    private final FragmentActivity mActivity;

    public CheckVersionHandler(FragmentActivity context) {
        super(context);
        this.mActivity = context;
    }


    public static CheckVersionHandler create(FragmentActivity context) {
        return new CheckVersionHandler(context);
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }

    @Override
    public boolean handle(final NextVersion engine, final Version version) {
        // 手动触发或非强制下载安装级别，可以弹出提示窗口：
        if (!Version.UpgradeLevel.FORCE_INSTALL.equals(version.upgradeLevel)) {
            // 强制级别不可取消
            final boolean closable = !Version.UpgradeLevel.FORCE_EACH.equals(version.upgradeLevel);
            VersionDialogFragment dialogFragment = VersionDialogFragment.newInstance(version, closable);
            dialogFragment.show(mActivity, new VersionDialogFragment.OnUpgradeClickListener() {
                @Override
                public void onClick(Version version) {
                    engine.setAutoInstall(false);
                    engine.upgrade(version);
                }
            }, new VersionDialogFragment.DownloadListener() {
                @Override
                public void onDownloadSuccess() {
                    engine.install(version);
                }
            });
            return true;
        } else {
            return false;
        }
    }
}
