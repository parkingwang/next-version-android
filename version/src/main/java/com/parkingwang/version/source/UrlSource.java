/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.source;

import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.version.Source;

import java.util.HashMap;
import java.util.Map;

/**
 * 版本更新地址源
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class UrlSource implements Source {

    private final String mUrl;
    private final String mMethod;

    private final Map<String, String> mParams = new HashMap<>();
    private final Map<String, String> mHeaders = new HashMap<>();

    public UrlSource(String url, String method) {
        if (!isValid(url)) {
            throw new IllegalArgumentException("非法的URL地址: " + url);
        }
        mUrl = url;
        mMethod = method;
    }

    @Override
    public String path() {
        return mUrl;
    }

    public String method() {
        return mMethod;
    }

    private boolean isValid(String uri){
        return StringKit.isNotEmpty(uri) && uri.startsWith("http");
    }

    public boolean hasParams(){
        return !mParams.isEmpty();
    }

    public boolean hasHeaders(){
        return !mHeaders.isEmpty();
    }

    public Map<String, String> params(){
        return new HashMap<>(mParams);
    }

    public Map<String, String> headers(){
        return new HashMap<>(mHeaders);
    }

    public UrlSource param(String name, Object value){
        mParams.put(name, String.valueOf(value));
        return this;
    }

    public UrlSource header(String name, Object value){
        mHeaders.put(name, String.valueOf(value));
        return this;
    }

    public static class Builder {

        private String mUrl;
        private String mMethod = "get";
        private final Map<String, String> mParams = new HashMap<>();
        private final Map<String, String> mHeaders = new HashMap<>();

        public Builder url(String url){
            mUrl = url;
            return this;
        }

        public Builder POST(){
            return method("post");
        }

        public Builder GET(){
            return method("get");
        }

        public Builder method(String method){
            mMethod = method;
            return this;
        }

        public Builder param(String name, Object value){
            return param(name, String.valueOf(value));
        }

        public Builder param(String name, String value){
            mParams.put(name, value);
            return this;
        }

        public Builder header(String name, String value){
            mHeaders.put(name, value);
            return this;
        }

        public Builder header(String name, Object value){
            return header(name, String.valueOf(value));
        }

        public UrlSource build(){
            final UrlSource src = new UrlSource(mUrl, mMethod);
            if (!mHeaders.isEmpty()) src.mHeaders.putAll(mHeaders);
            if (!mParams.isEmpty()) src.mParams.putAll(mParams);
            return src;
        }
    }
}
