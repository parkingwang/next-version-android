package com.parkingwang.version.support;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parkingwang.lang.Required;
import com.parkingwang.version.R;
import com.parkingwang.version.Version;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public abstract class VersionBlockingFragment extends DialogFragment {

    private static final String KEY_DATA_CLOSABLE = "data:closable";
    private static final String KEY_DATA_VERSION = "data:version";

    private final Required<OnUpgradeClickListener> mUpgradeClickListener = new Required<>();

    public void setupArgs(Version version, boolean closeable){
        final Bundle bundle = new Bundle(2);
        bundle.putBoolean(KEY_DATA_CLOSABLE, closeable);
        bundle.putParcelable(KEY_DATA_VERSION, version);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Version_Theme_FixedDialog);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentViewId(), container, false);
    }

    ////////

    public void show(final FragmentActivity activity, OnUpgradeClickListener listener) {
        mUpgradeClickListener.set(listener);
        if (!activity.isFinishing()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !activity.isDestroyed()) {
            this.show(activity.getSupportFragmentManager(), getTag());
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
    }

    //////

    protected void setupUpgradeButton(View contentView, int resId){
        contentView.findViewById(resId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Version version = getVersionInfo();
                try{
                    mUpgradeClickListener.getChecked()
                            .onClick(version);
                }finally {
                    VersionBlockingFragment.this.dismissAllowingStateLoss();
                }
            }
        });
    }

    protected void setupCloseButton(View contentView, int resId){
        setupCloseButton(contentView, resId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionBlockingFragment.this.dismissAllowingStateLoss();
            }
        });
    }

    protected void setupCloseButton(View contentView, int closeResId, View.OnClickListener clickListener) {
        // 关闭按钮
        if (isClosable()){
            final View view = contentView.findViewById(closeResId);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(clickListener);
        }else{
            contentView.findViewById(closeResId).setVisibility(View.GONE);
        }
    }

    protected void setupVersionInfo(View contentView, int versionNameId, int releaseNoteId){
        final Version data = getVersionInfo();
        final TextView versionView = (TextView) contentView.findViewById(versionNameId);
        final TextView noteView = (TextView) contentView.findViewById(releaseNoteId);
        if (data != null) {
            versionView.setText(data.name);
            noteView.setText(data.releaseNote);
        }
    }

    //////

    protected abstract int getContentViewId();

    //////

    protected Version getVersionInfo(){
        return getArguments().getParcelable(KEY_DATA_VERSION);
    }

    protected boolean isClosable(){
        return getArguments().getBoolean(KEY_DATA_CLOSABLE);
    }

    public interface OnUpgradeClickListener {
        void onClick(Version version);
    }

}
