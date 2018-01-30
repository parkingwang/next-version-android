/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.support;

import android.content.Context;
import android.os.Environment;

import com.parkingwang.lang.kit.StringKit;

import java.io.File;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public final class Paths {

    public final static String CACHE_DIR_NAME = "nv.files";

    public final static String PUBLIC_CACHE_PATH = findPublicDirectory() + "/" + CACHE_DIR_NAME;

    public static String resolvePublicPath(String fileName){
        return PUBLIC_CACHE_PATH + "/" + fileName;
    }

    public static String resolvePrivatePath(Context context, String fileName){
        return findPrivateDirectory(context) + "/" + fileName;
    }

    public static boolean createPublicPath(){
        return new File(PUBLIC_CACHE_PATH).mkdirs();
    }

    public static boolean isLocalPathValid(String path){
        return StringKit.isNotEmpty(path) && path.startsWith("/");
    }

    public static boolean isRemotePathValid(String path){
        return StringKit.isNotEmpty(path) && path.startsWith("http");
    }

    public static boolean isLocalPathExists(String path){
        return new File(path).exists();
    }

    public static String getReNameOf(File file, String mark){
        final String name = file.getName();
        final int index = name.lastIndexOf(".");
        final String ext = name.substring(index);
        final String rawName = name.substring(0, index);
        if (rawName.contains("(") && rawName.contains(")")) {
            return rawName.replaceAll("\\(\\d+\\)", "") + mark + ext;
        }else{
            return rawName + mark + ext;
        }
    }

    public static File renameIfExists(File origin, int index){
        if (origin.exists()){
            final String parent = origin.getParent();
            final String rename = Paths.getReNameOf(origin, "(" + index + ")");
            final File newFile;
            if (StringKit.isNotEmpty(parent)){
                newFile = new File(parent + File.separatorChar + rename);
            }else{
                newFile = new File(rename);
            }
            return renameIfExists(newFile, ++index);
        }else{
            return origin;
        }
    }

    ////

    private static String findPublicDirectory(){
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getPath();
    }

    private static String findPrivateDirectory(Context context){
        return context.getApplicationContext()
                .getCacheDir()
                .getPath();
    }
}
