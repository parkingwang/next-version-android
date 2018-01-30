/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.parkingwang.lang.Required;
import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.version.download.LocalFileApkDownloader;
import com.parkingwang.version.download.SilentNetworkApkDownloader;
import com.parkingwang.version.fetcher.MockAssetSourceFetcher;
import com.parkingwang.version.fetcher.RemoteHttpSourceFetcher;
import com.parkingwang.version.ihandler.RootedInstallHandler;
import com.parkingwang.version.ihandler.SystemInstallHandler;
import com.parkingwang.version.parser.SimpleJSONParser;
import com.parkingwang.version.support.FileHash;
import com.parkingwang.version.support.Paths;
import com.parkingwang.version.vhandler.DailyUpgradeVersionHandler;
import com.parkingwang.version.vhandler.ForceUpgradeVersionHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Android App版本检测及升级支持库。
 *
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */

public class NextVersion {

    private final Required<Version> mLocalVersion = new Required<>();
    private final Required<Scheduler> mScheduler = new Required<>();
    private final Required<VersionVerifier> mVersionVerifier = new Required<>();

    private final Required<Runnable> mOnNotFoundHandler = new Required<>();
    private final Required<Runnable> mOnCheckStartHandler = new Required<>();
    private final Required<Runnable> mOnCheckCompletedHandler = new Required<>();

    private final ArrayList<Source> mSources = new ArrayList<>();
    private final ArrayList<SourceFetcher> mSourceFetchers = new ArrayList<>();
    private final ArrayList<VersionParser> mVersionParsers = new ArrayList<>();
    private final ArrayList<VersionFoundHandler> mVersionFoundHandlers = new ArrayList<>();
    private final ArrayList<VersionInstallHandler> mVersionInstallHandlers = new ArrayList<>();
    private final ArrayList<ApkDownloader> mApkDownloader = new ArrayList<>();

    private boolean mIsForceSetUpgradeLevel = false;
    private boolean mIsAutoInstall = true;

    private final Required<ApkInfo> mApkInfo = new Required<>();

    public static NextVersion with(Context context) {
        return new NextVersion(context);
    }

    public NextVersion(final Context context) {
        initImpl(context)
                .setupDefaults(context);
    }

    private NextVersion initImpl(Context context) {
        addSource(new LocalFileSource(context));

        addSourceFetcher(new LocalFileSourceFetcher());
        addSourceFetcher(new MockAssetSourceFetcher(context));
        addSourceFetcher(new RemoteHttpSourceFetcher());

        addVersionParser(new SimpleJSONParser());

        addVersionFoundHandler(new DailyUpgradeVersionHandler(context));
        addVersionFoundHandler(new ForceUpgradeVersionHandler());

        addApkDownloader(new LocalFileApkDownloader());
        addApkDownloader(new SilentNetworkApkDownloader());

        addVersionInstallHandler(new SystemInstallHandler(context));
        addVersionInstallHandler(new RootedInstallHandler(context));
        return this;
    }

    private NextVersion setupDefaults(final Context context) {
        // 默认情况下，使用当前Context的版本作为校验版本；
        setLocalVersion(versionOfContext(context));
        // 使用默认版本校验器：比较VersionCode大小；
        setVersionVerifier(new VersionVerifier.VersionCodeVerifier());
        // 在新线程中执行版本校验请求
        runOn(Scheduler.NewThread.create());
        return this;
    }

    /**
     * 设置未发现新版本时的处理回调接口。
     *
     * @param runnable 回调接口
     * @return NextVersion
     */
    public NextVersion setVersionNotFoundHandler(Runnable runnable) {
        mOnNotFoundHandler.set(runnable);
        return this;
    }

    /**
     * 设置check开始时的回调接口
     *
     * @param runnable 回调接口
     * @return NextVersion
     */
    public NextVersion setOnCheckStartListener(Runnable runnable) {
        mOnCheckStartHandler.set(runnable);
        return this;
    }

    /**
     * 设置check结束时的回调接口
     *
     * @param runnable 回调接口
     * @return NextVersion
     */
    public NextVersion setOnCheckCompletedListener(Runnable runnable) {
        mOnCheckCompletedHandler.set(runnable);
        return this;
    }

    /**
     * 设置是否由用户手动触发。
     * 设置为用户手动触发检测更新，在处理App版本信息时，会主动忽略upgradeLevel，设置为推荐升级。
     *
     * @param manual 是否由用户手动触发
     * @return NextVersion
     */
    public NextVersion setManualMode(boolean manual) {
        mIsForceSetUpgradeLevel = manual;
        return this;
    }

    /**
     * 设置是否开启调试日志输出。默认关闭。
     *
     * @param isDebugEnabled 是否开启
     */
    public NextVersion setDebugEnabled(boolean isDebugEnabled) {
        AppLogger.DEBUG_ENABLED = isDebugEnabled;
        return this;
    }

    /**
     * 设置APK文件下载完成后，是否自动安装。
     *
     * @param autoInstall 自动安装
     */
    public NextVersion setAutoInstall(boolean autoInstall) {
        mIsAutoInstall = autoInstall;
        return this;
    }

    /**
     * 设置版本检测运行时所在的线程调调度器
     *
     * @param scheduler 线程调度器
     * @return NextVersion
     */
    public NextVersion runOn(Scheduler scheduler) {
        mScheduler.set(scheduler);
        return this;
    }

    /**
     * 发起新版本检测
     */
    public void check() {
        final Scheduler scheduler = mScheduler.getChecked();
        scheduler.submit(new Runnable() {

            @Override
            public void run() {
                mOnCheckStartHandler.ifPresent(new com.parkingwang.lang.Consumer<Runnable>() {
                    @Override
                    public void call(Runnable task) {
                        task.run();
                    }
                });
                try {
                    doCheck();
                } finally {
                    mOnCheckCompletedHandler.ifPresent(new com.parkingwang.lang.Consumer<Runnable>() {
                        @Override
                        public void call(Runnable task) {
                            task.run();
                        }
                    });
                }
            }

            private void doCheck() {
                final Version localVersion = mLocalVersion.getChecked();
                final Version newVersion = findLatestVersionFromAllSources(localVersion);
                AppLogger.d("本地版本：" + localVersion);
                AppLogger.d("远程版本：" + newVersion);
                if (newVersion.isValid()) {
                    AppLogger.d("发现新版本: " + newVersion);
                    for (VersionFoundHandler handler : mVersionFoundHandlers) {
                        if (handler.handle(NextVersion.this, newVersion)) {
                            break;
                        }
                    }
                } else {
                    AppLogger.d("未发现新版本或当前已是最新版本");
                    mOnNotFoundHandler.ifPresent(new com.parkingwang.lang.Consumer<Runnable>() {
                        @Override
                        public void call(Runnable task) {
                            task.run();
                        }
                    });
                }
            }
        });
    }

    /**
     * 更新指定版本文件
     *
     * @param version 版本信息文件
     */
    public void upgrade(final Version version) {
        // download apk file
        final Scheduler scheduler = mScheduler.getChecked();
        scheduler.submit(new Runnable() {

            @Override
            public void run() {
                AppLogger.d("请求更新版本，正在下载...");
                // 清除APK Info缓存
                mApkInfo.remove();
                // 下载APK文件
                final ApkInfo apkInfo = Stream.of(mApkDownloader)
                        .map(new Function<ApkDownloader, ApkInfo>() {
                            @Override
                            public ApkInfo apply(ApkDownloader downloader) {
                                return downloader.applyDownload(version);
                            }
                        })
                        .filter(new Predicate<ApkInfo>() {
                            @Override
                            public boolean test(ApkInfo value) {
                                return value.isValid();
                            }
                        })
                        .findFirst()
                        .orElse(ApkInfo.failed());
                // 安装APK文件
                if (apkInfo.isValid()) {
                    // 将下载的版本信息，更新到 LocalFileSource 缓存文件中:
                    Stream.of(mSources)
                            .filter(new Predicate<Source>() {
                                @Override
                                public boolean test(Source source) {
                                    return source instanceof LocalFileSource;
                                }
                            })
                            .map(new Function<Source, LocalFileSource>() {
                                @Override
                                public LocalFileSource apply(Source source) {
                                    return (LocalFileSource) source;
                                }
                            })
                            .findSingle()
                            .ifPresent(new Consumer<LocalFileSource>() {
                                @Override
                                public void accept(LocalFileSource localFileSource) {
                                    localFileSource.writeToPrivateCache(apkInfo, version);
                                }
                            });
                    // 自动安装
                    mApkInfo.set(apkInfo);
                    if (mIsAutoInstall || version.isLocalUri()) {
                        AppLogger.d("新版本APK文件已下载完成，正自动安装: " + apkInfo);
                        install(version);
                    } else {
                        AppLogger.d("新版本APK文件已下载完成，等待手动安装: " + apkInfo);
                    }
                } else {
                    AppLogger.d("新版本APK文件已下载完成，校验失败，无法安装: " + apkInfo);
                }
            }
        });
    }

    /**
     * 安装已经下载的Apk文件。
     * 如果APK文件未下载时调用时方法，会抛出IllegalStateException异常。
     *
     * @param version Version信息
     */
    public void install(Version version) {
        if (!mApkInfo.isPresent()) {
            throw new IllegalStateException("APK Info NOT FOUND(Not Download yet?)");
        }
        // 安装APK文件
        for (VersionInstallHandler handler : mVersionInstallHandlers) {
            if (handler.handle(NextVersion.this, version, mApkInfo.getUnchecked())) {
                break;
            }
        }
    }

    /**
     * 销毁NextVersion。
     * 关闭一些超过NextVersion生命周期的资源。
     */
    public void destroy() {
        if (mScheduler.isPresent()) {
            mScheduler.getChecked().shutdown();
        }
    }

    /**
     * 设置用于检测版本的本地版本信息对象。从服务器获取的版本信息将基于此版本信息来做对比。
     * 默认情况下，为创建NextVersion所指定的Context的版本信息。
     *
     * @param version 版本信息
     * @return NextVersion
     */

    public NextVersion setLocalVersion(Version version) {
        mLocalVersion.set(version);
        return this;
    }

    /**
     * 设置指定PackageInfo的版本为本地版本
     *
     * @param pi PackageInfo
     * @return NextVersion
     */
    public NextVersion setLocalVersionOfPackage(PackageInfo pi) {
        setLocalVersion(Version.local(pi.versionCode, pi.versionName));
        return this;
    }

    /**
     * 设置版本校验接口。用于判断本地版本与远程版本是否为最新。
     *
     * @param verifier 校验接口
     * @return NextVersion
     */
    public NextVersion setVersionVerifier(VersionVerifier verifier) {
        mVersionVerifier.set(verifier);
        return this;
    }

    ////////

    /**
     * 添加新版本检测源。
     *
     * @param source 新版本检测源
     * @return NextVersion
     */
    public NextVersion addSource(Source source) {
        mSources.add(source);
        return this;
    }

    /**
     * 添加检测源数据Fetcher。
     *
     * @param sourceFetcher SourceFetcher
     * @return NextVersion
     */
    public NextVersion addSourceFetcher(SourceFetcher sourceFetcher) {
        mSourceFetchers.add(sourceFetcher);
        return this;
    }

    /**
     * 添加版本信息解析Parser。
     *
     * @param parser Parser
     * @return NextVersion
     */
    public NextVersion addVersionParser(VersionParser parser) {
        mVersionParsers.add(parser);
        return this;
    }

    /**
     * 添加新版本信息处理器。
     *
     * @param handler 新版本信息处理器。
     * @return NextVersion
     */
    public NextVersion addVersionFoundHandler(VersionFoundHandler handler) {
        mVersionFoundHandlers.add(handler);
        Collections.sort(mVersionFoundHandlers, new Comparator<VersionFoundHandler>() {
            @Override
            public int compare(VersionFoundHandler o1, VersionFoundHandler o2) {
                return o1.priority() - o2.priority();
            }
        });
        return this;
    }

    /**
     * 添加APK文件下载器。
     *
     * @param downloader APK文件下载器
     * @return NextVersion
     */
    public NextVersion addApkDownloader(ApkDownloader downloader) {
        mApkDownloader.add(downloader);
        Collections.sort(mApkDownloader, new Comparator<ApkDownloader>() {
            @Override
            public int compare(ApkDownloader o1, ApkDownloader o2) {
                return o1.priority() - o2.priority();
            }
        });
        return this;
    }

    /**
     * 添加APK文件安装器
     *
     * @param handler 安装器
     * @return NextVersion
     */
    public NextVersion addVersionInstallHandler(VersionInstallHandler handler) {
        mVersionInstallHandlers.add(handler);
        Collections.sort(mVersionInstallHandlers, new Comparator<VersionInstallHandler>() {
            @Override
            public int compare(VersionInstallHandler o1, VersionInstallHandler o2) {
                return o1.priority() - o2.priority();
            }
        });
        return this;
    }

    ////////

    private Version findLatestVersionFromAllSources(final Version localVersion) {
        return Stream.of(mSources)
                .map(new Function<Source, Version>() {
                    @Override
                    public Version apply(Source source) {
                        final Version ver = findVersionFromSource(source);
                        AppLogger.d("更新源(" + source.path() + "), 返回版本：" + ver);
                        return ver;
                    }
                })
                .filter(new Predicate<Version>() {
                    @Override
                    public boolean test(Version version) {
                        final boolean passed = version.isValid() &&
                                mVersionVerifier.getChecked()
                                        .accept(localVersion, version);
                        AppLogger.d("版本校验接口处理，校验结果：" + (passed ? "通过" : "无效"));
                        return passed;
                    }
                }).min(new Comparator<Version>() {
                    @Override
                    public int compare(Version v1, Version v2) {
                        // 版本比较策略：
                        // 版本号相同时，比较Hash值：
                        //      1. Hash相同，本地优先；
                        //      2. Hash不同，远程优先；
                        // 版本号不同是，大的优先；
                        if (v1.code == v2.code) {
                            if (v1.isSameHash(v2)) {
                                if (v1.isLocalUri()) {
                                    return -1;
                                } else if (v2.isLocalUri()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else {
                                if (v1.isLocalUri()) {
                                    return 1;
                                } else if (v2.isLocalUri()) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            return v1.code - v2.code;
                        }
                    }
                })
                .map(new Function<Version, Version>() {
                    @Override
                    public Version apply(Version version) {
                        if (version.isValid() && mIsForceSetUpgradeLevel) {
                            AppLogger.d("强制转UpgradeLevel设置为默认状态");
                            return new Version(
                                    version.code, version.name, version.url, version.releaseNote,
                                    // 当用户设置为手动更新时，强制转UpgradeLevel设置为默认状态，用以强制显示版本提示窗:
                                    Version.UpgradeLevel.NOTIFY_EACH.ordinal(),
                                    version.fileHash, version.fileSize
                            );
                        } else {
                            return version;
                        }
                    }
                })
                .orElse(Version.invalid("更新源无有效版本数据"));
    }

    /**
     * 从更新源中获取版本信息
     *
     * @param source 更新源
     * @return 版本信息
     */
    private Version findVersionFromSource(final Source source) {
        final SourceFetcher.Response resp = Stream.of(mSourceFetchers)
                .map(new Function<SourceFetcher, SourceFetcher.Response>() {
                    @Override
                    public SourceFetcher.Response apply(SourceFetcher fetcher) {
                        try {
                            return fetcher.fetch(source);
                        } catch (IOException e) {
                            AppLogger.e("抓取器下载数据时发生错误", e);
                            return SourceFetcher.Response.notFound();
                        }
                    }
                })
                .filter(new Predicate<SourceFetcher.Response>() {
                    @Override
                    public boolean test(SourceFetcher.Response response) {
                        return response.isValid();
                    }
                })
                .findFirst()
                .orElse(SourceFetcher.Response.notFound());
        if (resp.isValid()) {
            return Stream.of(mVersionParsers)
                    .map(new Function<VersionParser, Version>() {
                        @Override
                        public Version apply(VersionParser parser) {
                            return parser.parse(resp.text);
                        }
                    })
                    .filter(new Predicate<Version>() {
                        @Override
                        public boolean test(Version version) {
                            AppLogger.d("校验版本：" + version);
                            // 从数据源中获取并解析出数据后，需要对版本数据做校验：
                            final boolean passed = version.isValid() &&
                                    checkPathValid(version); // 检查版本的下载地址是否完整；
                            AppLogger.d("校验结果：" + (passed ? "通过" : "无效"));
                            return passed;
                        }
                    })
                    .findFirst()
                    .orElse(Version.invalid("版本校验无效"));
        } else {
            AppLogger.e("更新源返回无效的响应数据");
            return Version.invalid("无效响应数据");
        }
    }

    ////////

    public static Version versionOfContext(Context context) {
        try {
            final PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return Version.local(pi.versionCode, pi.versionName);
        } catch (Exception e) {
            AppLogger.e("获取App版本信息出错", e);
            return Version.invalid("本地版本出错");
        }
    }

    ////////

    // 检查APK文件下载路径的有效性：
    private static boolean checkPathValid(Version version) {
        if (Paths.isLocalPathValid(version.url)) {
            // 如果是本地缓存文件，检查其文件Hash是否匹配。
            // 如果不匹配，可能是文件下载不完整。
            return Paths.isLocalPathExists(version.url)
                    && checkFileHashValid(version);
        } else {
            return Paths.isRemotePathValid(version.url);
        }
    }

    // 检查本地缓存文件的Hash是否匹配：
    private static boolean checkFileHashValid(Version version) {
        if (StringKit.isNotEmpty(version.fileHash)) {
            final String localHash = FileHash.md5(new File(version.url));
            if (!version.fileHash.equalsIgnoreCase(localHash)) {
                AppLogger.d("文件的Hash不匹配, 远程Hash: " + version.fileHash + ", 缓存Hash: " + localHash);
                return false;
            }
        }
        return true;
    }
}
