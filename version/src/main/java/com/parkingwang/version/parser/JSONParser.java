/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.parser;

import com.parkingwang.lang.kit.StringKit;
import com.parkingwang.version.AppLogger;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public abstract class JSONParser implements VersionParser {

    @Override
    final public Version parse(String responseText) {
        // 简单地检测响应数据是否为JSON字符串
        final boolean notEmpty = StringKit.isNotEmpty(responseText);
        if (notEmpty) {
            final String jsonText = responseText.trim();
            if (jsonText.startsWith("{") && jsonText.endsWith("}")) {
                try {
                    return parseJSON((JSONObject) new JSONTokener(responseText).nextValue());
                } catch (JSONException e) {
                    AppLogger.e("解析JSON数据时发生错误", e);
                    AppLogger.e("无法解析的JSON数据：" + responseText);
                    return Version.invalid("JSON错误");
                }
            }else{
                AppLogger.e("未识别JSON数据：" + responseText);
            }
        }
        return Version.invalid("无JSON数据");
    }

    protected abstract Version parseJSON(JSONObject json);

    protected static JSONObject getObject(JSONObject json, String fieldName) throws JSONException {
        return getObject(json, fieldName, new JSONObject());
    }

    protected static JSONObject getObject(JSONObject json, String fieldName, JSONObject defaultValue) throws JSONException {
        return json.has(fieldName) ? json.getJSONObject(fieldName) : defaultValue;
    }

    protected static int getInt(JSONObject json, String fieldName) throws JSONException {
        return getInt(json, fieldName, 0);
    }

    protected static int getInt(JSONObject json, String fieldName, int defaultValue) throws JSONException {
        return json.has(fieldName) ? json.getInt(fieldName) : defaultValue;
    }

    protected static String getString(JSONObject json, String fieldName) throws JSONException {
        return getString(json, fieldName, "");
    }

    protected static String getString(JSONObject json, String fieldName, String defaultValue) throws JSONException {
        return json.has(fieldName) ? json.getString(fieldName) : defaultValue;
    }

    protected static Long getLong(JSONObject json, String fieldName, long defValue) throws JSONException {
        return json.has(fieldName) ? json.getLong(fieldName) : defValue;
    }
}
