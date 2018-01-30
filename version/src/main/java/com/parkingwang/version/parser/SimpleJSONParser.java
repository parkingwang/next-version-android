/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.parser;

import com.parkingwang.version.AppLogger;
import com.parkingwang.version.Version;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class SimpleJSONParser extends JSONParser {

    @Override
    protected Version parseJSON(JSONObject root) {
        try{
            if (root.has("status") 
                    && root.has("data") 
                    && 200 == root.getInt("status")) {
                return parseDataField(root.getJSONObject("data"));
            }
        }catch (Exception e){
            AppLogger.e("解析JSON数据出错", e);
        }
        return Version.invalid("JSON出错");
    }

    private static Version parseDataField(JSONObject data) throws JSONException {
        return new Version(
                getInt(data, "versionCode", 0),
                getString(data, "versionName", ""),
                getString(data, "fileUrl", ""),
                getString(data, "releaseNote", ""),
                getInt(data, "upgradeLevel", 0),
                getString(data, "fileHash", ""),
                getLong(data, "fileSize", 0)
        );
    }

}
