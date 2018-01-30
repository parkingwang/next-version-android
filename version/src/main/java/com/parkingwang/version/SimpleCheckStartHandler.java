package com.parkingwang.version;

import android.content.Context;

public class SimpleCheckStartHandler extends ToastMessageHandler {

    public static SimpleCheckStartHandler create(Context context){
        return new SimpleCheckStartHandler(context);
    }

    public SimpleCheckStartHandler(Context context) {
        super("正在检测新版本", context);
    }

}