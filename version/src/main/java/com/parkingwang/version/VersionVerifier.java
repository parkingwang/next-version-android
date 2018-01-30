package com.parkingwang.version;

/**
 * 版本校验接口。
 * 用于比较本地版本信息与远程版本信息，返回是否可以更新的标志。
 * 默认情况下，仅比较两者的 versionCode 大小：如果远程版本的code大于本地版本的code则认为可以下载安装。
 * @author 陈小锅 (yoojiachen@gmail.com)
 */
public interface VersionVerifier {

    boolean accept(Version localVersion, Version remoteVersion);

    class VersionCodeVerifier implements VersionVerifier {

        @Override
        public boolean accept(Version localVersion, Version remoteVersion) {
            // 仅比较两者的versionCode大小
            return remoteVersion.code > localVersion.code;
        }
    }
}
