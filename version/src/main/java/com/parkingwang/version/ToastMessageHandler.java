package com.parkingwang.version;

import android.content.Context;
import android.widget.Toast;

public class ToastMessageHandler extends MainThreadHandler {

    public static ToastMessageHandler create(int message, Context context) {
        return create(context.getString(message), context);
    }

    public static ToastMessageHandler create(String message, Context context){
        return new ToastMessageHandler(message, context);
    }

    private final String mMessage;

    public ToastMessageHandler(String message, Context context) {
        super(context);
        mMessage = message;
    }

    @Override
    protected void runOnMainThread() {
        Toast.makeText(getContext(), mMessage, Toast.LENGTH_SHORT)
                .show();
    }
}