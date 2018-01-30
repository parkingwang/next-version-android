package com.parkingwang.version;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public abstract class MainThreadRunnable implements Runnable {

    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            runOnMainThread();
            return true;
        }
    });

    @Override
    public void run() {
        mMainThreadHandler.sendEmptyMessage(0);
    }

    protected abstract void runOnMainThread();

}
