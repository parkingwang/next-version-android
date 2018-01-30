/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.wave;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.TextView;

import com.parkingwang.version.wave.supports.DialogProgressBar;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public final class ProgressDialogActivity extends Activity {

    private static final String ACTION_FILTER = "key:next.version:download:progress";

    private final AtomicLong mStartTime = new AtomicLong(System.currentTimeMillis());

    private final Handler mDelayHandler = new Handler();

    private final BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final long totalLength = getTotalBytes(intent);
            final long progress = getProgress(intent);
            mProgressView.setValue(progress);
            final String fileSizeText = context.getString(R.string.app_size, convertToMB(totalLength));
            mAppSize.setText(fileSizeText);
            if (isDone(intent)) {
                mProgressView.setPiantComplete();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogActivity.this.finish();
                    }
                };
                // 如果下载得太快(少于4s就完成)，则延时0.8秒再退出
                if (System.currentTimeMillis() - mStartTime.get() < 4000) {
                    mDelayHandler.postDelayed(runnable, 800);
                } else {
                    runnable.run();
                }
            }
            // 强制关闭Activity
            if (forceClose(intent)){
                ProgressDialogActivity.this.finish();
            }
        }
    };

    private DialogProgressBar mProgressView;
    private TextView mAppSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(com.parkingwang.version.R.style.Version_Theme_FixedDialog);
        setContentView(R.layout.dialog_download_pregress);
        mProgressView = (DialogProgressBar) findViewById(R.id.progress_bar);
        mAppSize = (TextView) findViewById(R.id.percent);
        registerReceiver(mProgressReceiver, new IntentFilter(ACTION_FILTER));
        mStartTime.set(System.currentTimeMillis());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mProgressReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    //////

    public static void show(Context context) {
        final Intent intent = new Intent(context, ProgressDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void hide(Context context){
        // 当前ProgressDialogActivity在100%时会自动关闭
        // 但是由于某此原因，会导致下载失败。此时需要强制关闭加载窗口
        final Intent intent = new Intent(ACTION_FILTER);
        intent.putExtra("data.signal.close", true);
        context.sendBroadcast(intent);
    }

    ////

    private static String convertToMB(long bytes) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        return decimalFormat.format((double) (bytes / 1024) / 1024) + "MB";
    }

    public static void updateProgress(Context context, long totalLength, long progress, boolean isFinish) {
        final Intent intent = new Intent(ACTION_FILTER);
        intent.putExtra("data.total-length", totalLength);
        intent.putExtra("data.current-progress", progress);
        intent.putExtra("data.done", isFinish);
        context.sendBroadcast(intent);
    }

    private static long getTotalBytes(Intent intent) {
        return intent.getLongExtra("data.total-length", 0);
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
}
