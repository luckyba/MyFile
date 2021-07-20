package com.luckyba.myfile.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;

public class PermissionUtils {

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static String[] List_permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    public static int MY_FILE_REQUEST_CODE = 0;

    public static boolean useRunTimePermissions() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean checkManagementPerMission () {
        return Environment.isExternalStorageManager();
    }

    public static boolean hasListPermission(Activity activity, String[] list_permission) {
        if (useRunTimePermissions()) {
            for (int i = 0; i < list_permission.length; i++) {
                if (activity.checkSelfPermission(list_permission[i]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasPermission(Activity activity, String list_permission) {
        if (useRunTimePermissions()) {
           return activity.checkSelfPermission(list_permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestPermissions(Activity activity, String[] permission, int requestCode) {
        if (useRunTimePermissions()) {
            activity.requestPermissions(permission, requestCode);
        }
    }

    public static void requestPermissions(Fragment fragment, String[] permission, int requestCode) {
        if (useRunTimePermissions()) {
            fragment.requestPermissions(permission, requestCode);
        }
    }

    public static boolean shouldShowRational(Activity activity, String permission) {
        if (useRunTimePermissions()) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    public static boolean shouldAskForPermission(Activity activity, String[] permission) {
        if (useRunTimePermissions()) {
            for (int i = 0; i < permission.length; i++) {
                if (!hasPermission(activity, permission[i]) &&
                        (!hasAskedForPermission(activity, permission[i]) ||
                                shouldShowRational(activity, permission[i]))) {
                    return true;
                }
            }

        }
        return false;
    }


    public static void goToAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", MyApplication.getInstance().getPackageName(), null));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivityForResult(intent, 10);
    }

    public static boolean hasAskedForPermission(Activity activity, String permission) {
        return PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getBoolean(permission, false);
    }

    public static void markedPermissionAsAsked(Activity activity, String permission) {
        PreferenceManager
                .getDefaultSharedPreferences(activity)
                .edit()
                .putBoolean(permission, true)
                .apply();
    }

    public static void promptSettings(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permission Required");
        builder.setMessage(Html.fromHtml("We require your consent to additional permission in order to proceed. Please enable them in <b>Settings</b>"));
        builder.setPositiveButton("go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionUtils.goToAppSettings(activity);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // finish();
                Toast.makeText(activity, activity.getString(R.string.you_dont_have_management_permission_so_you_cant_use_some_funtions), Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
}