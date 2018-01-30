/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

import android.os.Parcel;
import android.os.Parcelable;

import com.parkingwang.lang.kit.HashKit;
import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.version.support.Paths;

import java.io.File;
import java.net.URI;

/**
 * 版本信息
 *
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */

public class Version implements Checkable, Parcelable {

    /**
     * App更新级别
     */
    public enum UpgradeLevel {
        /**
         * 通知级，可取消，每次提示
         */
        NOTIFY_EACH,

        /**
         * 通知级，可取消，每24小时提示
         */
        NOTIFY_DAILY,

        /**
         * 强制级，不可取消
         */
        FORCE_EACH,

        /**
         * 强制级，直接下载安装
         */
        FORCE_INSTALL;

        public static UpgradeLevel of(int rawUpgradeLevel){
            switch (rawUpgradeLevel){
                default:
                case 0: return NOTIFY_EACH;
                case 1: return NOTIFY_DAILY;
                case 2: return FORCE_EACH;
                case 3: return FORCE_INSTALL;
            }
        }
    }

    public final int code;
    public final String name;
    public final String url;
    public final String releaseNote;

    public final UpgradeLevel upgradeLevel;
    public final String fileHash;
    public final long fileSize;

    public Version(int code, String name, String url, String releaseNote, int upgradeLevel, String fileHash, long fileSize) {
        this.code = code;
        this.name = name;
        this.url = url;
        this.releaseNote = releaseNote;
        this.upgradeLevel = UpgradeLevel.of(upgradeLevel);
        this.fileHash = fileHash;
        this.fileSize = fileSize;
    }

    @Override
    public boolean isValid(){
        // 基本版本信息:
        // - 版本号必须大于0；
        // - URL地址不能为空；
        return code > 0 && StringKit.isNotEmpty(url);
    }

    public String getFileName(){
        try{
            String fileName = new File(URI.create(url).getPath()).getName();
            if (fileName.endsWith(".apk")){
                return fileName;
            }else{
                return generateFileName();
            }
        }catch (Exception e){
            return generateFileName();
        }
    }

    public boolean isLocalUri() {
        return Paths.isLocalPathValid(this.url);
    }

    public boolean isSameHash(Version version) {
        if (StringKit.isEmpty(this.fileHash)) {
            return false;
        } else {
            return this.fileHash.equalsIgnoreCase(version.fileHash);
        }
    }

    private String generateFileName(){
        return (HashKit.toMd5Hex(url) + "-" + name + "-" + code + ".apk");
    }

    @Override
    public String toString() {
        return "Version{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", upgradeLevel=" + upgradeLevel +
                ", fileHash='" + fileHash + '\'' +
                ", fileSize=" + fileSize +
                ", url='" + url + '\'' +
                ", releaseNote='" + releaseNote + '\'' +
                '}';
    }

    public static Version local(int versionCode, String versionName){
        return new Version(versionCode, versionName, null, null, 0, "", 0);
    }

    public static Version invalid(String name) {
        return new Version(0, name, null, null, 0, "", 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.releaseNote);
        dest.writeInt(this.upgradeLevel == null ? -1 : this.upgradeLevel.ordinal());
        dest.writeString(this.fileHash);
        dest.writeLong(this.fileSize);
    }

    protected Version(Parcel in) {
        this.code = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
        this.releaseNote = in.readString();
        int tmpUpgradeLevel = in.readInt();
        this.upgradeLevel = tmpUpgradeLevel == -1 ? null : UpgradeLevel.values()[tmpUpgradeLevel];
        this.fileHash = in.readString();
        this.fileSize = in.readLong();
    }

    public static final Creator<Version> CREATOR = new Creator<Version>() {
        @Override
        public Version createFromParcel(Parcel source) {
            return new Version(source);
        }

        @Override
        public Version[] newArray(int size) {
            return new Version[size];
        }
    };
}
