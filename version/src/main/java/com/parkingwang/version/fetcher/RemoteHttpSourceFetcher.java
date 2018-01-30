/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.fetcher;

import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.parkingwang.version.AppLogger;
import com.parkingwang.version.Source;
import com.parkingwang.version.SourceFetcher;
import com.parkingwang.version.source.UrlSource;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

/**
 * 根据指定版本更新源信息，获取更新服务器的响应数据
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public final class RemoteHttpSourceFetcher implements SourceFetcher {

    public static final String TAG = "RemoteHttpSourceFetcher";

    public final OkHttpClient mHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .followRedirects(false)
            .retryOnConnectionFailure(true)
            .addInterceptor(new LogInterceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    if (AppLogger.DEBUG_ENABLED) {
                        return super.intercept(chain);
                    } else {
                        return chain.proceed(chain.request());
                    }
                }
            })
            .build();

    @Override
    public Response fetch(Source source) throws IOException {
        if (source instanceof UrlSource) {
            final okhttp3.Request request = createHttpRequest((UrlSource) source);
            final okhttp3.Response response = mHttpClient
                    .newCall(request)
                    .execute();
            if (response.isSuccessful()){
                AppLogger.d("Fetch source, SUCCESS : " + source.path());
                return Response.found(response.body().string());
            }else{
                AppLogger.d("Fetch source, FAILED : " + source.path());
                return Response.notFound();
            }
        }else{
            return Response.notFound();
        }
    }

    /**
     * 当真正调用发送请求时，使用此方法来创建OKHttp3的请求对象
     * @return OKHttp3请求对象
     */
    public okhttp3.Request createHttpRequest(UrlSource source){
        final String url = source.path();
        final okhttp3.Request.Builder builder = newHttpBuilder(url);
        // Headers
        if (source.hasHeaders()) {
            for (Map.Entry<String, String> header : source.headers().entrySet()) {
                final String value = header.getValue();
                // 先清空默认重复的Header字段
                builder.removeHeader(header.getKey());
                if (value != null) {
                    builder.addHeader(header.getKey(), header.getValue());
                }
            }
        }
        // Params
        final String method = StringKit.isEmpty(source.method()) ? "get" : source.method().toLowerCase();
        if (source.hasParams()) {
            final String queryString = toQueryString(source.params());
            // 如果传递了参数，检查当前HttpMethod是否为get, head，这两个Method不允许使用RequestBody，
            // 将参数接收到URL中
            if (methodShouldHasQueryStringInUrl(method)){
                builder.url(url + (url.contains("?") ? "&" : "?") + queryString);
            }else{
                final RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        queryString);
                builder.method(method, body);
            }
        }else{
            switch (method.toLowerCase()) {
                case "get": builder.get(); break;
                case "post": builder.post(Util.EMPTY_REQUEST); break;
            }
        }
        return builder.build();
    }

    private static okhttp3.Request.Builder newHttpBuilder(String url){
        final HttpUrl httpUrl = HttpUrl.parse(url);
        return new okhttp3.Request.Builder()
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8")
                .addHeader("Host", httpUrl.host())
                .addHeader("User-Agent", "NextVersion/1.2")
                .url(url);
    }

    private static boolean methodShouldHasQueryStringInUrl(String method){
        return "get".equals(method) || "head".equals(method);
    }

    private static String toQueryString(Map<String, String> params){
        final List<String> kvs = new ArrayList<>();
        for (Map.Entry<String, String> param : params.entrySet()) {
            kvs.add(param.getKey() + "=" + URLEncoder.encode(param.getValue()));
        }
        return StringKit.join(kvs, "&");
    }
}
