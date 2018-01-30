package com.parkingwang.version.vhandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionFoundHandler;
import com.parkingwang.version.support.ContextX;
import com.parkingwang.version.support.Priority;

import java.util.concurrent.TimeUnit;

/**
 * 处理App升级等级为NotifyDaily的版本信息，检查是否超过24小时。
 * @author 陈小锅 (yoojiachen@gmail.com)
 */
public class DailyUpgradeVersionHandler extends ContextX implements VersionFoundHandler {

    private static final String KEY_TIME = "daily:last-notify-time";

    public DailyUpgradeVersionHandler(Context context) {
        super(context);
    }

    @Override
    public int priority() {
        return Priority.HIGH_H2;
    }

    @Override
    public boolean handle(NextVersion engine, Version version) {
        if (Version.UpgradeLevel.NOTIFY_DAILY.equals(version.upgradeLevel)){
            final SharedPreferences sp = getContext().getSharedPreferences("next-version.conf", Context.MODE_PRIVATE);
            final long lastNotifyTime = sp.getLong(KEY_TIME, 0);
            if (lastNotifyTime == 0 // 初始时
                    || moreThan24Hours(lastNotifyTime)){ // 超过24小时
                updateTime(sp);
                return false;
            }else{
                return true; // 拦截后续版本提示
            }
        }else{
            return false;
        }
    }

    private void updateTime(SharedPreferences sp){
        sp.edit()
                .putLong(KEY_TIME, System.currentTimeMillis())
                .apply();
    }

    private static boolean moreThan24Hours(long time){
        return 24 <=
                TimeUnit.HOURS.convert(System.currentTimeMillis() - time, TimeUnit.MILLISECONDS);
    }
}
