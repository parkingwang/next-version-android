/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.source;

import com.parkingwang.version.Source;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public final class MockAssetSource implements Source {

    @Override
    public String path() {
        return "mock.json";
    }
}
