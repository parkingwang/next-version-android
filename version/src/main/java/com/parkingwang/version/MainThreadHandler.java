package com.parkingwang.version;

import android.content.Context;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public abstract class MainThreadHandler extends MainThreadRunnable {

    private final Context mContext;

    public MainThreadHandler(Context context) {
        mContext = context;
    }

    protected Context getContext(){
        return mContext;
    }

}
