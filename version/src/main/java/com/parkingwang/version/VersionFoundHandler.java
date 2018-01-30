/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

/**
 * 新版本更新提示接口。
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public interface VersionFoundHandler {

    int priority();

    /**
     * @return 返回 True 表示当前接口已处理版本提示，后续接口将被中断。否则返回 False。
     */
    boolean handle(NextVersion engine, Version version);

}
