package com.hm.viscosityauto.utils;

import android.content.Context;
import android.os.Environment;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
 
public class CrashHandler implements Thread.UncaughtExceptionHandler {
 
    private static CrashHandler instance;
    private Context context;
 
    private CrashHandler(Context context) {
        this.context = context;
    }
 
    public static void init(Context context) {
        instance = new CrashHandler(context);
        Thread.setDefaultUncaughtExceptionHandler(instance);
    }
 
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        // 可以选择结束程序
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(10);
    }
 
    private void handleException(Throwable ex) {
        if (ex == null) {
            return;
        }
 
        // 使用StringWriter来获取异常堆栈信息
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
 
        String crashReport = sw.toString();
 
        // 写入SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/crash_report.log");
                fos.write(crashReport.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 
        // 也可以发送到服务器
        // sendCrashReportToServer(crashReport);
    }
 
    // 发送崩溃报告到服务器的方法（示例代码，根据实际情况实现）
    // private void sendCrashReportToServer(String crashReport) {
    //     // 实现发送崩溃报告到服务器的逻辑
    // }
}