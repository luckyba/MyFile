package com.luckyba.myfile.about;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.luckyba.myfile.MainActivity;
import com.luckyba.myfile.R;
import com.luckyba.myfile.app.MyApplication;
import com.luckyba.myfile.utils.PermissionUtils;

public class WelcomeScreen extends AppCompatActivity {
    private Button btnTurnOn;
    private LinearLayout layoutDeniedPermissionLayout;
    private static final String TAG = WelcomeScreen.class.getSimpleName();
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        layoutDeniedPermissionLayout = (LinearLayout) findViewById(R.id.id_access_permissions_layout);
        btnTurnOn= (Button) findViewById(R.id.id_btn_turn_on);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessStorage();

        }
        btnTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.getInstance().trackEvent("Button turn on","Permissions allow button","FileManger lite");
                accessStorage();
            }
        });
    }

    private void showActivity () {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MyApplication.getInstance(), MainActivity.class);
            startActivity(intent);
            finish();
        }, 100);
    }

    private void accessStorage() {
        boolean hasWriteStoragePermission = PermissionUtils.hasListPermission(this, PermissionUtils.List_permission);
        if (!hasWriteStoragePermission) {
            boolean showRequestAgain = ActivityCompat.shouldShowRequestPermissionRationale(WelcomeScreen.this, PERMISSION_WRITE_STORAGE);
            Log.e(TAG, "showRequestAgain: " + showRequestAgain);
            if (showRequestAgain) {
                new AlertDialog.Builder(this).setMessage("Storage permission is required")
                        .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(WelcomeScreen.this, PermissionUtils.List_permission,
                                        PermissionUtils.MY_FILE_REQUEST_CODE);
                            }
                        }).setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layoutDeniedPermissionLayout.setVisibility(View.VISIBLE);
                    }
                }).show();
                return;
            } else {
                PermissionUtils.requestPermissions(this, PermissionUtils.List_permission, PermissionUtils.MY_FILE_REQUEST_CODE);
                return;
            }
        } else {
            showActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.MY_FILE_REQUEST_CODE) {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        showActivity();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Permission Denied
                        layoutDeniedPermissionLayout.setVisibility(View.VISIBLE);
                        SharedPreferences pref = getSharedPreferences("fileManager", 0);
                        if (!pref.getBoolean("is_camera_requested", false)) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("is_camera_requested", true);
                            editor.apply();
                            return;
                        }
                        boolean showRequestAgain = ActivityCompat.shouldShowRequestPermissionRationale(WelcomeScreen.this, PERMISSION_WRITE_STORAGE);
                        if (showRequestAgain) {
                            //true,
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Permission Required");
                            builder.setMessage("Storage Permission is required");
                            builder.setPositiveButton("DENY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(WelcomeScreen.this, new String[]{PERMISSION_WRITE_STORAGE},
                                            PermissionUtils.MY_FILE_REQUEST_CODE);
                                }
                            });
                            builder.show();
                        } else {
                            promptSettings();
                        }
                    } else {
                        Log.e(TAG, "last else");
                    }
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void promptSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage(Html.fromHtml("We require your consent to additional permission in order to proceed. Please enable them in <b>Settings</b>"));
        builder.setPositiveButton("go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionUtils.goToAppSettings(getParent());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Welcome screen");
    }
}
