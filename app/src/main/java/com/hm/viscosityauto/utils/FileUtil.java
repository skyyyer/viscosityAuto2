package com.hm.viscosityauto.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {


    //读取内置data目录下文件
    public static String readDataFile(Context context, String fileName) {
        String res = "";
        try {
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer);
            fin.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "readDataFile Error!" + e.getMessage());
        }
        return res;
    }


    public static List<File> getFilesList(Context context) {
        // 获取内部存储的Files目录
        File internalDataDir = context.getFilesDir();


        // 确保目录存在
        if (internalDataDir != null && internalDataDir.exists()) {
            Log.e("internalDataDir", internalDataDir.getAbsolutePath());

            // 获取该目录下的所有文件
            File[] files = internalDataDir.listFiles();
            if (files != null) {
                return new ArrayList<>(Arrays.asList(files));
            }
        }
        return new ArrayList<>();
    }

    //写入内置data目录下文件
    public static void writeDataFile(Context context, String fileName, String content) {
        try {
            FileOutputStream fut = context.openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
            byte[] bytes = content.getBytes();
            fut.write(bytes);
            fut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "writeDataFile Error!" + e.getMessage());
        }
    }

    public static Uri FilePath2Uri(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return Uri.EMPTY;
    }

    /**
     * 从指定的APK文件中获取版本号。
     *
     * @param context 应用上下文
     * @param apkFile APK文件
     * @return 返回APK的版本号，如果获取失败则返回-1
     */
    public static int extractVersionCodeFromApk(Context context, File apkFile) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            // 使用PackageManager解析APK文件
            packageInfo = packageManager.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
            if (packageInfo != null) {
                return packageInfo.versionCode;
            }
        } catch (Exception e) {
            // 处理解析APK时可能出现的异常
            e.printStackTrace();
        }
        return -1; // 如果解析失败，返回-1
    }



    public static void installApk(Context context,String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName()+".provider", new File(filePath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }



    public static String getStoragePath(Context context, boolean isUsb) {
        String path = "";
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz;
        Class<?> diskInfoClaszz;
        try {
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            diskInfoClaszz = Class.forName("android.os.storage.DiskInfo");
            Method StorageManager_getVolumes = Class.forName("android.os.storage.StorageManager").getMethod("getVolumes");
            Method VolumeInfo_GetDisk = volumeInfoClazz.getMethod("getDisk");
            Method VolumeInfo_GetPath = volumeInfoClazz.getMethod("getPath");
            Method DiskInfo_IsUsb = diskInfoClaszz.getMethod("isUsb");
            Method DiskInfo_IsSd = diskInfoClaszz.getMethod("isSd");
            List<Object> List_VolumeInfo = (List<Object>) StorageManager_getVolumes.invoke(mStorageManager);
            if (List_VolumeInfo != null) {
                for (int i = 0; i < List_VolumeInfo.size(); i++) {
                    Object volumeInfo = List_VolumeInfo.get(i);
                    Object diskInfo = VolumeInfo_GetDisk.invoke(volumeInfo);
                    if (diskInfo == null) continue;
                    boolean sd = (boolean) DiskInfo_IsSd.invoke(diskInfo);
                    boolean usb = (boolean) DiskInfo_IsUsb.invoke(diskInfo);
                    File file = (File) VolumeInfo_GetPath.invoke(volumeInfo);

                    if (isUsb == usb) {//usb
                        if (file != null) {
                            path = file.getAbsolutePath();
                        }
                    } else if (!isUsb == sd) {//sd
                        if (file != null) {
                            path = file.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}

