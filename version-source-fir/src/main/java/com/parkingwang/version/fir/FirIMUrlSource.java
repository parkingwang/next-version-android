package com.parkingwang.version.fir;

import android.content.Context;

import com.parkingwang.version.source.UrlSource;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public class FirIMUrlSource extends UrlSource {

    public static FirIMUrlSource fromPackageName(String packageName, String token){
        return new FirIMUrlSource(packageName, token);
    }

    public static FirIMUrlSource fromContext(Context context, String token){
        return new FirIMUrlSource(context, token);
    }

    public FirIMUrlSource(String packageName, String token){
        super(url(packageName), "get");
        param("api_token", token);
    }

    public FirIMUrlSource(Context context, String token) {
        this(context.getPackageName(), token);
    }

    private static String url(String packageName){
        return "http://api.fir.im/apps/latest/" + packageName;
    }
}
