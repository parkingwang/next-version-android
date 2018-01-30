package com.parkingwang.version.pkw;

import com.parkingwang.version.source.UrlSource;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public class PkwUrlSource extends UrlSource {

    public PkwUrlSource(String hostUri, String production) {
        super(hostUri + "/releases/versions/" + production, "get");
        param("appType", "0");
    }

    public PkwUrlSource userId(String userId) {
        param("userId", userId);
        return this;
    }

    public PkwUrlSource channel(String channel) {
        param("channel", channel);
        return this;
    }

    public PkwUrlSource cityName(String cityName) {
        param("cityName", cityName);
        return this;
    }

    public PkwUrlSource cityCode(String cityCode) {
        param("cityCode", cityCode);
        return this;
    }

    public PkwUrlSource versionCode(int versionCode) {
        param("versionCode", versionCode);
        return this;
    }

    public PkwUrlSource versionName(String versionName) {
        param("versionName", versionName);
        return this;
    }

    public PkwUrlSource networkType(int networkType) {
        param("networkType", networkType);
        return this;
    }

    public PkwUrlSource provinceName(String provinceName) {
        param("provinceName", provinceName);
        return this;
    }
}
