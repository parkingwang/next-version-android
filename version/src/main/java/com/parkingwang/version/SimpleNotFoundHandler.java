package com.parkingwang.version;

import android.content.Context;

public class SimpleNotFoundHandler extends ToastMessageHandler {

    public static SimpleNotFoundHandler create(Context context){
        return new SimpleNotFoundHandler(context);
    }

    public SimpleNotFoundHandler(Context context) {
        super(context.getString(R.string.nv_is_latest_version), context);
    }

}