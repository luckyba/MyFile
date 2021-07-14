package com.luckyba.myfile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;


public class Utils {

    private Utils() {
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int getAvailableInternalStoragePercentage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation") long totalBlocks = stat.getBlockCount();
        long totalSize = totalBlocks * blockSize;
        @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
        long availableSize = availableBlocks * blockSize;
        Log.d("here is", "" + ((availableSize * 100) / totalSize));
        int size = (int) ((availableSize * 100) / totalSize);
        return 100 - size;
    }

    public static String formatSize(long size, String tag) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


    public static String getAvailableExternalMemorySize() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            @SuppressWarnings("deprecation") long blockSize = stat.getBlockSize();
            @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
            return Utils.formatSize(availableBlocks * blockSize, "free");
        } else {
            return "0";
        }
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d("getPath", path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize, "free");
    }


    public static int getAvailableExternalStoragePercentage() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            @SuppressWarnings("deprecation") long totalBlocks = stat.getBlockCount();
            long totalSize = totalBlocks * blockSize;
            @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            Log.d("here is", "" + ((availableSize * 100) / totalSize));
            int size = (int) ((availableSize * 100) / totalSize);
            return 100 - size;
        } else {
            return 0;
        }
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm= (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

}
