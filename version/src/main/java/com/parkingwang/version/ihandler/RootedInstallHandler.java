package com.parkingwang.version.ihandler;

import android.content.Context;

import com.parkingwang.version.ApkInfo;
import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Version;
import com.parkingwang.version.VersionInstallHandler;
import com.parkingwang.version.support.ContextX;
import com.parkingwang.version.support.Priority;

import java.io.PrintWriter;

/**
 * @author 陈永佳 (chenyongjia@parkingwang, yoojiachen@gmail.com)
 */
public class RootedInstallHandler extends ContextX implements VersionInstallHandler{

    public RootedInstallHandler(Context context) {
        super(context);
    }

    @Override
    public int priority() {
        return Priority.LOW;
    }

    @Override
    public boolean handle(NextVersion engine, Version version, ApkInfo apkInfo) {
        // 判断Root状态，如果可用由静默安装App
        return hasRootPermission() &&
                installBySuperuser(apkInfo.path);
    }

    private static boolean installBySuperuser(String apkPath){
        return exeSuCommand(
                ("chmod 777 " + apkPath),
                "export LD_LIBRARY_PATH=/vendor/lib:/system/lib",
                ("pm install -r " + apkPath),
                "exit"
        );
    }

    private static boolean hasRootPermission(){
        return exeSuCommand();
    }

    private static boolean exeSuCommand(String...commands){
        PrintWriter writer;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            writer = new PrintWriter(process.getOutputStream());
            for (String cmd : commands){
                writer.println(cmd);
            }
            writer.flush();
            writer.close();
            return 0 == process.waitFor();
        } catch (Exception e) {
            // NOP
        }finally{
            if(process != null){
                process.destroy();
            }
        }
        return false;
    }
}
