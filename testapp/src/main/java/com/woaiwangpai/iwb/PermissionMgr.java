package com.woaiwangpai.iwb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionMgr {
    private static final PermissionMgr sInstance = new PermissionMgr();

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] PERMISSIONS = new String[] {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.GET_TASKS};

    private static final String WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private List<String> mPermissionList = new ArrayList<String>();

    private PermissionMgr() {}

    public static PermissionMgr getInstance() {
        return sInstance;
    }

    public void requestPermissions(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        mPermissionList.clear();
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }

        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(context, mPermissionList.toArray(new String[]{}), PERMISSION_REQUEST_CODE);
        }
    }

    public boolean deniedExternalStoragePermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return true;
        }
        return false;
    }

    public void requestExternalStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (PERMISSION_REQUEST_CODE != requestCode) {
            return;
        }

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PERMISSION_GRANTED) {
                mPermissionList.remove(permissions[i]);
            }
        }

        if (mPermissionList.isEmpty()) {
            return;
        }

        if (showConfirmDialog(activity)) {
            requestPermissions(activity);
            return;
        }

        askForPermission(activity);
    }

    private boolean showConfirmDialog(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        for (String permission : mPermissionList) {
            // 还可以弹框确认
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }

        return false;
    }

    private void askForPermission(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permission!");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName())); // 根据包名打开对应的设置界面
                activity.startActivity(intent);
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }
}
