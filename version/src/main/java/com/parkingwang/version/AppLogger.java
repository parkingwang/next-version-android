package com.parkingwang.version;

import android.util.Log;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class AppLogger {

    private static final String TAG = "NextVersion";

    public static boolean DEBUG_ENABLED = false;

    public static void d(String message) {
        if (DEBUG_ENABLED) {
            Log.w(TAG, message);
        }
    }

    public static void e(String message, Throwable e) {
        Log.e(TAG, message, e);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }
}
