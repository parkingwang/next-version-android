package com.parkingwang.version.wave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.parkingwang.version.Version;
import com.parkingwang.version.support.VersionBlockingFragment;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public class VersionDialogFragment extends VersionBlockingFragment {

    public static VersionDialogFragment newInstance(Version version, boolean closable) {
        VersionDialogFragment fragment = new VersionDialogFragment();
        fragment.setupArgs(version, closable);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_version_wave;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupCloseButton(view, R.id.close);
        setupUpgradeButton(view, R.id.upgrade);
        setupVersionInfo(view, R.id.version_name, R.id.release_note);
    }
}
