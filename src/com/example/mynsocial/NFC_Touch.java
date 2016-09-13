package com.example.mynsocial;

import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynsocial.FbFriendDialog.DialogError;

public class NFC_Touch extends Activity implements OnClickListener{
	private static final String TAG = "nfcFragment";
	NfcAdapter mNfcAdapter;
	String inputText=null;
	byte statusByte;
	String payload="";
	TextView t1;
	String[] info;
	String a,b;
	String userid="";
	int vid=0;
	Add_Contact ana=new Add_Contact();
	Button back;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_nfc);
		
		TextView texttouch=(TextView) findViewById(R.id.texttouch1);
		back=(Button) findViewById(R.id.button1);
		back.setOnClickListener(this);
		
		
		
		t1=(TextView) findViewById(R.id.t11);
		SharedPreferences sp = this.getSharedPreferences("com.example.mynsocial",MODE_PRIVATE);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
        	Toast.makeText(this, "NFC apdater  is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } 

		Bundle a =getIntent().getExtras();
		userid = a.getString("userid");//自己的userid
		vid=a.getInt("vid");
		Log.i(TAG, "userid="+userid+",vid="+vid);
		if(vid==1){
			String name = sp.getString("name", null);
			String phone=sp.getString("phone", null);
			String address=sp.getString("address", null);
			String comname=sp.getString("comname", null);
			String job=sp.getString("job", null);
			String email=sp.getString("email", null);
			String mNote=comname+job;
			inputText=name+","+phone+","+address+","+comname+","+job+","+email+","+mNote;
	        NdefMessage message=create_RTD_TEXT_NdefMessage(inputText);  
	        //mNfcAdapter.enableForegroundNdefPush(this, message);
	        mNfcAdapter.setNdefPushMessage(message, this);
		}else if(vid==2){
	        NdefMessage message=create_RTD_TEXT_NdefMessage(userid);  
	        mNfcAdapter.setNdefPushMessage(message, this);
	        Log.i(TAG, "userid="+userid+",vid="+vid);
		}else if(vid==3){
			String name = sp.getString("name", null);
			String phone=sp.getString("phone", null);
			String address=sp.getString("address", null);
			String comname=sp.getString("comname", null);
			String job=sp.getString("job", null);
			String email=sp.getString("email", null);
			String mNote=comname+job;
			inputText=name+","+phone+","+address+","+comname+","+job+","+email+","+mNote+","+userid;
	        NdefMessage message=create_RTD_TEXT_NdefMessage(inputText);  
	        mNfcAdapter.setNdefPushMessage(message, this);
	        Log.i(TAG, "userid="+userid+",vid="+vid);
	        
		}
	}
	
    NdefMessage create_RTD_TEXT_NdefMessage(String inputText){
    	
    	Locale locale= new Locale("en","US");
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
		
		boolean encodeInUtf8=false;
	    Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
	    int utfBit = encodeInUtf8 ? 0 : (1 << 7);
	    byte status = (byte) (utfBit + langBytes.length);
	   
	    byte[] textBytes = inputText.getBytes(utfEncoding);
	    
	    byte[] data = new byte[1 + langBytes.length + textBytes.length];
	    data[0] = (byte) status;
	    System.arraycopy(langBytes, 0, data, 1, langBytes.length);
	    System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
	    
	    NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
	    		NdefRecord.RTD_TEXT, new byte[0], data);
       NdefMessage message= new NdefMessage(new NdefRecord[] { textRecord}); 
       return message;
    	
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }
    
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(NFC_Touch.this, MainPage.class);
		startActivity(intent); 
	}
    
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
    	Log.i(TAG, "---------------------------------------");
    	final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        String[][] techList = new String[][]{};
        adapter.enableForegroundDispatch(activity, pendingIntent, null, techList);
    }


    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    public void processIntent(Intent intent) {
    	 NdefMessage[] messages = getNdefMessages(getIntent());
         for(int i=0;i<messages.length;i++){
         	 for(int j=0;j<messages[0].getRecords().length;j++){
         		 NdefRecord record = messages[i].getRecords()[j];
         		 statusByte=record.getPayload()[0];
         		 int languageCodeLength= statusByte & 0x3F; //mask value in order to find language code length 
         		 int isUTF8=statusByte-languageCodeLength;
         		 if(isUTF8==0x00){
         			 payload=new String(record.getPayload(),1+languageCodeLength,record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-8"));
         		 }
         		 else if (isUTF8==-0x80){
         			 payload=new String(record.getPayload(),1+languageCodeLength,record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-16"));
         		 }
         		
         		info=payload.split(",");//inputtext的東西
         		
         	 }
         }
         if(vid==1){//card exchange
             int a=ana.addContact(this, info[0], info[2], info[1],info[6],info[5]); 
    		 Intent intenta = new Intent();
    	     intenta.setClass(NFC_Touch.this, MainPage.class);
    		 startActivity(intenta);
             if(a !=-1){
            	 Toast toast = Toast.makeText(this,"成功加入通訊錄!!", Toast.LENGTH_SHORT);
            	 toast.show();
             }else{
            	 Toast toast = Toast.makeText(this,"加入通訊錄失敗!!", Toast.LENGTH_SHORT);
            	 toast.show();
             }
         }else if(vid==2){//add fb friend
	         new FbFriendDialog(this, payload, new FbFriendDialog.DialogListener() {
					//user.getUsername()
	            	
				      @Override
				      public void onError(DialogError e)
				      {
				          Log.d("UserTimeline", "FbFriendDialog onError: " + e);
				      }
	
				      @Override
				      public void onComplete(Bundle values)
				      {
				          if (values != null)
				          {
				              String result = values.getString("action");
	
				              if (result != null && result.equals("1"))
				              {
				                  // friend request sent or confirmed
				              }
				              else if (result != null && result.equals("0"))
				              {
				                  // friend request declined or canceled
				              }
				          }
				      }
	
				      @Override
				      public void onCancel()
				      {
				          Log.d("UserTimeline", "FbFriendDialog onCancel: ");
				      }
	
					@Override
					public void onFacebookError(
							com.example.mynsocial.FbFriendDialog.FacebookError e) {
						 Log.d("UserTimeline", "FbFriendDialog onFacebookError: " + e);
						
					}
				  }).show();
         }else if(vid==3){//do exchange and add friend
             int a=ana.addContact(this, info[0], info[2], info[1],info[6],info[5]); 
             if(a !=-1){
             	 Toast toast = Toast.makeText(this,"成功加入通訊錄!!", Toast.LENGTH_SHORT);
             	 toast.show();
             }else{
             	 Toast toast = Toast.makeText(this,"加入通訊錄失敗!!", Toast.LENGTH_SHORT);
             	 toast.show();
             }
             
 	         new FbFriendDialog(this, info[7], new FbFriendDialog.DialogListener() {
 					//user.getUsername()
 	            	
 				      @Override
 				      public void onError(DialogError e)
 				      {
 				          Log.d("UserTimeline", "FbFriendDialog onError: " + e);
 				      }
 	
 				      @Override
 				      public void onComplete(Bundle values)
 				      {
 				          if (values != null)
 				          {
 				              String result = values.getString("action");
 	
 				              if (result != null && result.equals("1"))
 				              {
 				                  // friend request sent or confirmed
 				              }
 				              else if (result != null && result.equals("0"))
 				              {
 				                  // friend request declined or canceled
 				              }
 				          }
 				      }
 	
 				      @Override
 				      public void onCancel()
 				      {
 				          Log.d("UserTimeline", "FbFriendDialog onCancel: ");
 				      }
 	
 					@Override
 					public void onFacebookError(
 							com.example.mynsocial.FbFriendDialog.FacebookError e) {
 						 Log.d("UserTimeline", "FbFriendDialog onFacebookError: " + e);
 						
 					}
 				  }).show();
 	         
         }
    }
    
    
    NdefMessage[] getNdefMessages(Intent intent) {
        
        NdefMessage[] msgs = null;
    	 if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
    		 Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        if (rawMsgs != null) {
	            msgs = new NdefMessage[rawMsgs.length];
	            for (int i = 0; i < rawMsgs.length; i++) {
	                msgs[i] = (NdefMessage) rawMsgs[i];
	            }
	        } else {
	            byte[] empty = new byte[] {};
	            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
	            NdefMessage msg = new NdefMessage(new NdefRecord[] {
	                record
	            });
	            msgs = new NdefMessage[] {
	                msg
	            };
	        }
    	 }else {
    		  Log.d("Peer to Peer 2", "Unknown intent.");
	            finish();
	        }
         
        return msgs;
    }
}