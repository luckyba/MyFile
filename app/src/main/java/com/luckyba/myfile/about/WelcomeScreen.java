package com.luckyba.myfile.about;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

    @RequiresApi(api = Build.VERSION_CODES.R)
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void accessStorage() {
        boolean hasWriteStoragePermission = PermissionUtils.hasListPermission(this, PermissionUtils.List_permission);
        if (!hasWriteStoragePermission) {
            PermissionUtils.requestPermissions(this, PermissionUtils.List_permission, PermissionUtils.MY_FILE_REQUEST_CODE);
        } else {
            if (!Environment.isExternalStorageManager()) {
                promptSettings();
            } else {
                showActivity();
            }
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.MY_FILE_REQUEST_CODE) {
                if (grantResults.length > 0) {
                    if (Environment.isExternalStorageManager()) {
                        showActivity();
                    } else  {
                        // Permission Denied
                        layoutDeniedPermissionLayout.setVisibility(View.VISIBLE);
                    }
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void promptSettings() {
        Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                layoutDeniedPermissionLayout.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onResume() {
        super.onResume();
        if (!Environment.isExternalStorageManager()) {
            promptSettings();
        } else {
            showActivity();
        }
        MyApplication.getInstance().trackScreenView("Welcome screen");
    }

}
