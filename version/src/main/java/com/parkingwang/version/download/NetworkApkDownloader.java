/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.download;

import com.parkingwang.version.ApkDownloader;
import com.parkingwang.version.ApkInfo;
import com.parkingwang.version.AppLogger;
import com.parkingwang.version.Version;
import com.parkingwang.version.support.Paths;
import com.parkingwang.version.support.Priority;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public abstract class NetworkApkDownloader extends ApkDownloader.AcceptedApkDownloader {

    private static final String TAG = "NetworkApkDownloader";

    private final OnDownloadProgressListener mProgressListener;

    public NetworkApkDownloader(OnDownloadProgressListener progressListener) {
        mProgressListener = progressListener;
        if (Paths.createPublicPath()) {
            AppLogger.d("创建下载缓存目录：" + Paths.PUBLIC_CACHE_PATH);
        }
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }

    @Override
    protected boolean accept(Version version) {
        return Paths.isRemotePathValid(version.url);
    }

    @Override
    protected ApkInfo download(Version version) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), mProgressListener))
                                .build();
                    }
                })
                .build();
        final String path = Paths.resolvePublicPath(version.getFileName());
        final File saveFile = Paths.renameIfExists(new File(path), 0);
        AppLogger.d("准备下载APK文件，地址：" + version.url + "，保存到：" + saveFile.getAbsolutePath());
        mProgressListener.onStart(version);
        try {
            final Request request = new Request.Builder().url(version.url).build();
            final Response response = client.newCall(request).execute();
            final BufferedSink sink = Okio.buffer(Okio.sink(saveFile));
            sink.writeAll(response.body().source());
            sink.close();
            return ApkInfo.ofFile(saveFile);
        } catch (IOException e) {
            AppLogger.e("下载文件发生错误", e);
            AppLogger.e("下载错误, 删除文件..." + (saveFile.delete() ? "成功" : "失败"));
            return ApkInfo.failed();
        }finally {
            mProgressListener.onCompleted();
        }
    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody mResponseBody;
        private final OnDownloadProgressListener mProgressListener;
        private BufferedSource mBufferedSource;

        ProgressResponseBody(ResponseBody responseBody, OnDownloadProgressListener progressListener) {
            this.mResponseBody = responseBody;
            this.mProgressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return mResponseBody.contentType();
        }

        @Override
        public long contentLength() {
            return mResponseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (mBufferedSource == null) {
                mBufferedSource = Okio.buffer(source(mResponseBody.source()));
            }
            return mBufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {

                private long mTotalLength = mResponseBody.contentLength();
                private long mCurrentRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    final long read = super.read(sink, byteCount);
                    mCurrentRead += read != -1 ? read : 0;
                    mProgressListener.onUpdate(
                            mTotalLength,
                            mCurrentRead,
                            (mTotalLength==mCurrentRead));
                    return read;
                }
            };
        }
    }

    public interface OnDownloadProgressListener {
        void onStart(Version targetVersion);
        void onUpdate(long totalBytes, long currentBytes, boolean done);
        void onCompleted();
    }
}
