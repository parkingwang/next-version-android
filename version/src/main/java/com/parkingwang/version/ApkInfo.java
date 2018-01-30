/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

import android.os.Parcel;
import android.os.Parcelable;

import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.version.support.FileHash;

import java.io.File;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class ApkInfo implements Checkable, Parcelable {

    public static final int INVALID_LENGTH = -12345678;

    public static final String APK_MIME = "application/vnd.android.package-archive";

    public final long size;
    public final String hash;
    public final String path;
    public final String fileName;

    public ApkInfo(long size, String hash, String path, String fileName) {
        this.size = size;
        this.hash = hash;
        this.path = path;
        this.fileName = fileName;
    }

    @Override
    public boolean isValid(){
        return size > 0 && StringKit.isNotEmpty(path);
    }

    @Override
    public String toString() {
        return "ApkInfo{" +
                "size=" + size +
                ", hash='" + hash + '\'' +
                ", path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public static ApkInfo ofFile(File file){
        return new ApkInfo(file.length(), FileHash.md5(file), file.getAbsolutePath(), file.getName());
    }

    public static ApkInfo failed(){
        return new ApkInfo(INVALID_LENGTH, "", "", "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.size);
        dest.writeString(this.hash);
        dest.writeString(this.path);
        dest.writeString(this.fileName);
    }

    protected ApkInfo(Parcel in) {
        this.size = in.readLong();
        this.hash = in.readString();
        this.path = in.readString();
        this.fileName = in.readString();
    }

    public static final Parcelable.Creator<ApkInfo> CREATOR = new Parcelable.Creator<ApkInfo>() {
        @Override
        public ApkInfo createFromParcel(Parcel source) {
            return new ApkInfo(source);
        }

        @Override
        public ApkInfo[] newArray(int size) {
            return new ApkInfo[size];
        }
    };

}