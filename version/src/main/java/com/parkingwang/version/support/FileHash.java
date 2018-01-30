/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.support;

import com.parkingwang.lang.kit.StringKit;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class FileHash {

    public static String md5(File file){
        try{
            final FileInputStream fileInputStream = new FileInputStream(file);
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
            byte[] bytes = new byte[1024];
            // read all file content
            while (digestInputStream.read(bytes) > 0);
            return StringKit.hex(digest.digest());
        }catch (Exception e){
            return "";
        }
    }
}
