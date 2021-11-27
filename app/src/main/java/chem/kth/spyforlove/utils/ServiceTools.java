package chem.kth.spyforlove.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

import chem.kth.MainApplication;

public class ServiceTools {
    private static String LOG_TAG = ServiceTools.class.getName();

    public static boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) MainApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }
}