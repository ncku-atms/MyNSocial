package com.example.mynsocial;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

public class Add_Contact {//加入通訊錄
	public int addContact(Activity mAcitvity, String name, String address, String number, String mNote, String email) {  
        int contactID = -1;  
//      t1.setText(name+address+number+mNote+email);
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();  
        int rawContactID = ops.size();  
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)  
             .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(RawContacts.ACCOUNT_NAME, null).build());  

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)  
             .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)  
             .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE).withValue(StructuredName.DISPLAY_NAME, name).build());  

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)  
             .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)  
             .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE).withValue(Phone.NUMBER, number)  
             .withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE).build());  

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)  
             .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)  
             .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)  
             .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address).build());  
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)  
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)  
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)  
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, mNote).build()); 


        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)  
             .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)  
             .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)  
             .withValue(ContactsContract.CommonDataKinds.Note.NOTE, mNote).build());  
        
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                .withValue(Email.ADDRESS, email)
                .withValue(Email.TYPE, Email.TYPE_WORK)
                .build());
        
        try {  
             ContentResolver mResolver = mAcitvity.getContentResolver();  
             ContentProviderResult[] mlist = mResolver.applyBatch(ContactsContract.AUTHORITY, ops);  
             Uri myContactUri = mlist[0].uri;  
             int lastSlash = myContactUri.toString().lastIndexOf("/");  
             int length = myContactUri.toString().length();  
             contactID = Integer.parseInt((String) myContactUri.toString().subSequence(lastSlash + 1, length));  
        } catch (Exception e) {  
             e.printStackTrace();  
        }  
        return contactID;  
   } 
}
