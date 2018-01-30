/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.fir;

import com.parkingwang.version.Version;
import com.parkingwang.version.parser.JSONParser;

import org.json.JSONObject;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public class FirIMVersionParser extends JSONParser {

    public static FirIMVersionParser create(){
        return new FirIMVersionParser();
    }

    /*
    {
        "name": "停车王智慧停车",
        "version": "1298",
        "changelog": "- Update: v2017.5-1 移除对ARM 3.0版本以下的兼容处理；\n- Update: v2017.5-1 移除默认登录用户配置；\n- Update: v2017.5-1 优化AppConfigManager部分逻辑；\n",
        "updated_at": 1494298884,
        "versionShort": "2017.5-1",
        "build": "1298",
        "installUrl": "http://download.fir.im/v2/app/install/584a8bfaca87a80db70008b5?download_token=289e21d23d9c11c2c1299fa1bbbae68c&source=update",
        "install_url": "http://download.fir.im/v2/app/install/584a8bfaca87a80db70008b5?download_token=289e21d23d9c11c2c1299fa1bbbae68c&source=update",
        "direct_install_url": "http://download.fir.im/v2/app/install/584a8bfaca87a80db70008b5?download_token=289e21d23d9c11c2c1299fa1bbbae68c&source=update",
        "update_url": "http://fir.im/pw4pad3",
        "binary": {
            "fsize": 32551074
         }
    }
     */
    @Override
    protected Version parseJSON(JSONObject json) {
        // 检查JSON结构是否符合Fir的响应JSON格式
        if (isFirResponseJSON(json)){
            try{
                return new Version(
                        getInt(json, "version"),
                        getString(json, "versionShort"),
                        getString(json, "install_url"),
                        getString(json, "changelog"),
                        0, // UpgradeLevel
                        "",// File Hash
                        getInt(getObject(json, "binary"), "fsize")
                );
            }catch (Exception e){
                e.printStackTrace();
                return Version.invalid("解析FIR.JSON出错");
            }
        }else{
            return Version.invalid("FIR.JSON异常");
        }
    }

    private static boolean isFirResponseJSON(JSONObject json){
        return json.has("updated_at")
                && json.has("versionShort")
                && json.has("binary")
                && json.has("update_url")
                && json.has("direct_install_url");
    }
}
