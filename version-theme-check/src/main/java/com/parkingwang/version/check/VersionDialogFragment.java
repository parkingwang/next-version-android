package com.parkingwang.version.check;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parkingwang.lang.Required;
import com.parkingwang.version.Version;
import com.parkingwang.version.check.support.DrawHookView;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 占迎辉 (zhanyinghui@parkingwang.com)
 * @version 2017/10/16
 */

public class VersionDialogFragment extends DialogFragment {

    private final Required<OnUpgradeClickListener> mListenerRequired = new Required<>();
    private final Required<DownloadListener> mDownloadListenerRequired = new Required<>();

    private static final String ACTION_UPDATE_PROGRESS = "key:next.version:download:progress";
    private static final String ACTION_UPDATE_COMPLETED = "key:next.version:download:completed";

    private static final String KEY_DATA_CLOSABLE = "data:closable";
    private static final String KEY_DATA_VERSION = "data:version";

    private DrawHookView mProgressView;
    private TextView mVersionName;
    private final AtomicLong mStartTime = new AtomicLong();
    private final Handler mDelayHandler = new Handler(Looper.getMainLooper());

    private final BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String act = intent.getAction();
            if (ACTION_UPDATE_PROGRESS.equals(act)) {
                onProgress(intent);
            } else if (ACTION_UPDATE_COMPLETED.equals(act)) {
                onCompleted();
            }
        }

        private void onProgress(Intent intent) {
            final long progress = getProgress(intent);
            if (progress == 1) {
                mStartTime.set(System.currentTimeMillis());
            }
            mProgressView.setValue(progress);
            mVersionName.setText(getString(R.string.download_percent) + progress + "%");
            // 强制关闭Activity
            if (forceClose(intent)) {
                VersionDialogFragment.this.dismiss();
            }
        }

        private void onCompleted() {
            mProgressView.addOnCompleteListener(new DrawHookView.OnCompleteListener() {
                @Override
                public void onDrawComplete() {
                    try {
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                VersionDialogFragment.this.dismiss();
                            }
                        };
                        // 如果下载得太快(少于4s就完成)，则延时0.8秒再退出
                        if (System.currentTimeMillis() - mStartTime.get() < 10000) {
                            mDelayHandler.postDelayed(runnable, 800);
                        } else {
                            runnable.run();
                        }
                    } finally {
                        mDownloadListenerRequired.getChecked().onDownloadSuccess();
                    }
                }
            });

        }
    };

    private void onDrawComplete() {
        mProgressView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                boolean isOver = mProgressView.drawComplete();
                if (isOver) {
                    mDownloadListenerRequired.getChecked().onDownloadSuccess();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IntentFilter filter = new IntentFilter(ACTION_UPDATE_PROGRESS);
        filter.addAction(ACTION_UPDATE_COMPLETED);
        getActivity().registerReceiver(mProgressReceiver, filter);
        setStyle(DialogFragment.STYLE_NO_TITLE, com.parkingwang.version.R.style.Version_Theme_FixedDialog);
        setCancelable(false);
    }

    public static VersionDialogFragment newInstance(Version version, boolean closable) {
        VersionDialogFragment fragment = new VersionDialogFragment();
        fragment.setupArgs(version, closable);
        return fragment;
    }

    public void setupArgs(Version version, boolean closeable) {
        final Bundle bundle = new Bundle(2);
        bundle.putBoolean(KEY_DATA_CLOSABLE, closeable);
        bundle.putParcelable(KEY_DATA_VERSION, version);
        setArguments(bundle);
    }

    public static void updateProgress(Context context, long totalLength, long progress, boolean isFinish) {
        final Intent intent = new Intent(ACTION_UPDATE_PROGRESS);
        intent.putExtra("data.total-length", totalLength);
        intent.putExtra("data.current-progress", progress);
        intent.putExtra("data.done", isFinish);
        context.sendBroadcast(intent);
    }

    public static void updateCompleted(Context context) {
        final Intent intent = new Intent(ACTION_UPDATE_COMPLETED);
        context.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_check, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupCloseButton(view, R.id.close);
        setupUpgradeButton(view, R.id.upgrade);
        mProgressView = (DrawHookView) view.findViewById(R.id.progress_view);
        mVersionName = (TextView) view.findViewById(R.id.version_name);
        mVersionName.setText(getString(R.string.check_new_version, "4.7.0"));

    }

    public void show(FragmentActivity activity, OnUpgradeClickListener onUpgradeClickListener, DownloadListener downloadListener) {
        mListenerRequired.set(onUpgradeClickListener);
        mDownloadListenerRequired.set(downloadListener);
        if (!activity.isFinishing() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !activity.isDestroyed()) {
            show(activity.getSupportFragmentManager(), getTag());
        }

    }

    protected void setupUpgradeButton(final View contentView, final int resId) {
        final CheckBox upgrade = (CheckBox) contentView.findViewById(resId);
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgrade.setClickable(false);
                final Version version = getVersionInfo();
                mListenerRequired.getChecked()
                        .onClick(version);
            }
        });
    }

    protected void setupCloseButton(View contentView, int resId) {
        setupCloseButton(contentView, resId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionDialogFragment.this.dismiss();
            }
        });
    }

    protected void setupCloseButton(View contentView, int closeResId, View.OnClickListener clickListener) {
        // 关闭按钮
        if (isClosable()) {
            final View view = contentView.findViewById(closeResId);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(clickListener);
        } else {
            contentView.findViewById(closeResId).setVisibility(View.GONE);
        }
    }


    protected Version getVersionInfo() {
        return getArguments().getParcelable(KEY_DATA_VERSION);
    }

    protected boolean isClosable() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getBoolean(KEY_DATA_CLOSABLE);
        } else {
            return false;
        }
    }

    private static long getProgress(Intent intent) {
        return intent.getLongExtra("data.current-progress", 0);
    }

    private static boolean isDone(Intent intent) {
        return intent.getBooleanExtra("data.done", false);
    }

    private static boolean forceClose(Intent intent) {
        return intent.getBooleanExtra("data.signal.close", false);
    }

    public static void hide(Context context) {
        // 当前ProgressDialogActivity在100%时会自动关闭
        // 但是由于某此原因，会导致下载失败。此时需要强制关闭加载窗口
        final Intent intent = new Intent(ACTION_UPDATE_PROGRESS);
        intent.putExtra("data.signal.close", true);
        context.sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mProgressReceiver);
    }

    public interface OnUpgradeClickListener {
        void onClick(Version version);
    }

    public interface DownloadListener {
        void onDownloadSuccess();
    }
}
