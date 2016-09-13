/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mynsocial;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity implements AccelerometerListener{
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;



    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    NfcAdapter mNfcAdapter;
    byte statusByte;
    private String payload="";
    String inputText;//放自己的
    String address;//放別人的
    int count = 10;
    private Handler handler1 = new Handler();//時間的
    private int Shakecnt=0;
    byte[] writeBuf,readBuf;
    String writeMessage,readMessage,fbcaption;
    TextView title,rule,status;
    int myscore=0,youscore=0;
    //Facebook facebook = new Facebook("1534724866793625");
    // JSON parser class
 	JSONParser jsonParser = new JSONParser();
 	// url to update product
 	private static final String url_update_product = "http://140.116.54.216/MyNSocial/update_product.php";
 	private static final String TAG_SUCCESS = "success";
 	String fb_id;//放自己fb id
 	int wincount;
 	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	String finalscore;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.activity_fight);
        TextView text_touch=(TextView) findViewById(R.id.texttouch);
        
        
        title=(TextView) findViewById(R.id.titletext);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        SharedPreferences sp = this.getSharedPreferences("loginPrefs",MODE_PRIVATE);
		fb_id = sp.getString("fb_id", fb_id); 
        inputText=mBluetoothAdapter.getAddress();//取得藍芽MAC
		NdefMessage message=create_RTD_TEXT_NdefMessage(inputText);  
        mNfcAdapter.setNdefPushMessage(message, this);
 
    }
    
    
    

    @Override
    public void onStart() {
        super.onStart();
        
        if(D) Log.e(TAG, "++ ON START ++");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);                   
        } else {// Otherwise, setup the chat session
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
        if(D) Log.e(TAG, "+ ON RESUME +");
        
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {       	
        	processIntent(getIntent());
        	Log.e(TAG, "+++ ACTION +++");
        }
        
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
               mChatService.start();
            }
        }
        
        if (AccelerometerManager.isSupported(this)) {
            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }

    private void setupChat() {
        Log.d(TAG, "++setupChat()++");
        
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        
        

    }
    

    @Override
    public synchronized void onPause() {
        super.onPause();       
        Log.e(TAG, "+++ ON PAUSE +++");
    }
    
    
    @Override
    public void onNewIntent(Intent intent) {
    	
    	setIntent(intent);      
        
    }
    
    

    @Override
    public void onStop() {
        super.onStop();       
        Log.e(TAG, "+++onStop+++");
        if(D) Log.e(TAG, "-- ON STOP --");
        if (AccelerometerManager.isListening()) {
            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
        }
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
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "+++onDestroy+++");
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) { 
            //Start Accelerometer Listening
        	AccelerometerManager.stopListening();
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
    	Log.e(TAG, "+++ sendMessage +++");
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }



    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {//藍芽的handler
        @Override
        public void handleMessage(Message msg) {
        	Log.e(TAG, "+++ handleMessage +++");
            switch (msg.what) {
            case MESSAGE_WRITE:
            	Log.e(TAG, "+++ MESSAGE_WRITE +++");
                writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                writeMessage = new String(writeBuf);
                myscore=Integer.parseInt(writeMessage);
                Log.e(TAG, "+++ myscore +++"+myscore);
                //publishFeedDialog();
                break;
            case MESSAGE_READ:
            	Log.e(TAG, "+++ MESSAGE_READ: +++");
                readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                readMessage = new String(readBuf, 0, msg.arg1);
                youscore=Integer.parseInt(readMessage);
                Log.e(TAG, "+++ youscore +++"+youscore);
                FinalResult();
               // mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                //Toast.makeText(getApplicationContext(), readMessage+"read"+writeMessage, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_DEVICE_NAME:
            	Log.e(TAG, "+++ MESSAGE_DEVICE_NAME +++");
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
            	setContentView(R.layout.activity_fight2);
                // Send a message using content of the edit text widget
                //設定定時要執行的方法
                	handler1.removeCallbacks(updateTimer);
        		//設定Delay的時間
                	handler1.postDelayed(updateTimer, 1000);
                break;
            case MESSAGE_TOAST:
            	Log.e(TAG, "+++ MESSAGE_TOAST +++");
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    public void FinalResult(){
		loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
		loginPrefsEditor = loginPreferences.edit();
    	if(myscore>youscore){
    		fbcaption="Win";
    		wincount = loginPreferences.getInt("win",0);				
			loginPrefsEditor.putInt("win", wincount+1);
			loginPrefsEditor.commit();
			new SaveProductDetails().execute();
    		Log.e(TAG, "+++ i win+++");
    	}else if(myscore<youscore){
    		fbcaption="Lose";
    		Log.e(TAG, "+++ i lost +++");
    	}else{
    		fbcaption="Deuce";
    	}
    	
    	finalscore=myscore+":"+youscore;
//    	publishFeedDialog();
    	
    	
    	Intent intent = new Intent();
		intent.setClass(BluetoothChat.this, Result.class);
		Bundle bundle = new Bundle();
		if(fbcaption.equals("Win")){
			bundle.putString("fbcaption", "I win the game!");
			bundle.putString("score", finalscore);
			intent.putExtras(bundle);
		}else if(fbcaption.equals("Lose")){
			bundle.putString("fbcaption", "I lose the game!");
			bundle.putString("score", finalscore);
			intent.putExtras(bundle);
		}else if(fbcaption.equals("Deuce")){
			bundle.putString("fbcaption", "We deuce!");
			bundle.putString("score", finalscore);
			intent.putExtras(bundle);
		}
		//BluetoothChat.this.finish();
		startActivity(intent);
    }
    
    
    
    
    
    
    
    
    
    

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.e(TAG, "+++ onActivityResult +++");
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                finish();
            }
        }
    }

    private void  connectDevice(Intent data, boolean secure) {
    	Log.e(TAG, "+++  connectDevice +++");
        // Get the device MAC address
        String mac_address = address;
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac_address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
        
        
    }


    NdefMessage create_RTD_TEXT_NdefMessage(String inputText){
    	
    	Log.e(TAG, "+++ create_RTD +++");
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
    
    void processIntent(Intent intent) {
    	Log.e(TAG, "+++ processintent +++");
    	
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
         		//messageText.setText("Text received: "+ payload+",  "+isUTF8+"");
         		address=payload;
         		Intent serverIntent =new Intent();
         		connectDevice(serverIntent, true);
//         		Toast.makeText(this, address, Toast.LENGTH_LONG).show();
         	 }
         }
    }
    
    NdefMessage[] getNdefMessages(Intent intent) {
   	 Log.e(TAG, "+++ getNdefMessages +++");
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
    
    
    
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 //-------------------------------main2------------------------------------------------
    
    private Runnable updateTimer = new Runnable() {
		public void run() {
			final TextView time = (TextView) findViewById(R.id.timer);			
			if (count > 0) {
				time.setText(Integer.toString(count-1));
				count--;
				handler1.postDelayed(updateTimer, 1000);
				}else{
				time.setText("時間到!!");
				sendMessage(Shakecnt+"");
				}
		}
    };
    
    

    public void onAccelerationChanged(float x, float y, float z) {
        // TODO Auto-generated method stub
         
    }
 
    public void onShake(float force) {
     // Do your stuff here
	 TextView t2=(TextView) findViewById(R.id.shcnt);
	 Shakecnt++;
	 Log.d(TAG, "++Shake="+Shakecnt+"+++");
     // Called when Motion Detected
	 t2.setText(Shakecnt+"");
    }
 

// public void postOnMyWall() {
//	 
//	 facebook.dialog(this, "feed", new DialogListener() {
//	  
//	        @Override
//	        public void onFacebookError(FacebookError e) {
//	        }
//	  
//	        @Override
//	        public void onError(DialogError e) {
//	        }
//	  
//	        @Override
//	        public void onComplete(Bundle values) {
//	        }
//	  
//	        @Override
//	        public void onCancel() {
//	        }
//	    });
//	}
    
    
    
    //-----------------save to db--------------------------------------------------------------------------------------
	 /**
		 * Background Async Task to  Save product Details
		 * */
class SaveProductDetails extends AsyncTask<String, String, String> {

//			/**
//			 * Before starting background thread Show Progress Dialog
//			 * */
//			@Override
//			protected void onPreExecute() {
//				super.onPreExecute();
//				pDialog = new ProgressDialog(Battle.this);
//				pDialog.setMessage("aaaa");
//				pDialog.setIndeterminate(false);
//				pDialog.setCancelable(true);
//				pDialog.show();
//			}

			/**
			 * Saving product
			 * */
			protected String doInBackground(String... args) {
				
				 

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("fb_id", fb_id));
				params.add(new BasicNameValuePair("win", wincount+""));//丟累加的win
				

				// sending modified data through http request
				// Notice that update product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(url_update_product, "POST", params);

				// check json success tag
//				try {
//					int success = json.getInt(TAG_SUCCESS);
//					
//					if (success == 1) {
//						// successfully updated
//						Intent i = getIntent();
//						// send result code 100 to notify about product update
//						setResult(100, i);
//						finish();
//					} else {
//						// failed to update product
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}

				return null;
			}
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
