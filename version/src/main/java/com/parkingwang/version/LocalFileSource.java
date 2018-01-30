/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

import android.content.Context;

import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.version.support.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
final class LocalFileSource implements Source {

    private final static String JSON_FILE = "next-version.json";

    private final Context mContext;

    public LocalFileSource(Context context) {
        mContext = context;
    }

    @Override
    public String path(){
        // 使用App私有目录来保存缓存信息
        return Paths.resolvePrivatePath(mContext, JSON_FILE);
    }

    public void writeToPrivateCache(ApkInfo apk, Version version){
        AppLogger.d("写入JSON缓存信息...");
        String text = "";
        try{
            text = toJSONObject(version, apk.path).toString();
        }catch (JSONException e){
            AppLogger.e("生成新版本的JSON对象时发生错误", e);
        }
        if (StringKit.isNotEmpty(text)){
            final String path = path();
            try{
                final File file = new File(path);
                file.createNewFile();
                final BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeUtf8(text);
                sink.close();
            }catch (IOException e){
                AppLogger.e("写入新版本JSON数据时发生错误", e);
            }
        }
    }

    private static JSONObject toJSONObject(Version version, String overrideUrl) throws JSONException {
        final JSONObject output = new JSONObject();
        output.put("status", 200);
        output.put("msg", "CACHED-JSON-DATA");
        final JSONObject data = new JSONObject();
        data.put("upgradeLevel", version.upgradeLevel.ordinal());
        data.put("versionCode", version.code);
        data.put("versionName", version.name);
        data.put("releaseNote", version.releaseNote);
        data.put("fileSize", version.fileSize);
        data.put("fileHash", version.fileHash);
        data.put("fileUrl", overrideUrl);
        output.put("data", data);
        return output;
    }

}
