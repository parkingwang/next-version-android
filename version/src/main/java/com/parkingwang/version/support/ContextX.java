/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.support;

import android.content.Context;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public abstract class ContextX {

    private final Context mContext;

    public ContextX(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

}
