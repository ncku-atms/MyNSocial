package com.example.mynsocial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;


public class MainActivity extends FragmentActivity {
 
    private static final String TAG = "LoginFragment";
    private UiLifecycleHelper uiHelper;
    private TextView userInfoTextView;
    LoginButton authButton;
    private SharedPreferences loginPreferences;
    private static final String url_update_product = "http://140.116.54.216/MyNSocial/create_product.php";
    private static final String TAG_SUCCESS = "success";
    JSONParser jsonParser = new JSONParser();
    String username,userid;
  //  private TextView userName;

    
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        //userName = (TextView) findViewById(R.id.user_name);
        userInfoTextView = (TextView) findViewById(R.id.userInfoTextView);

    }
    
//    private Session.StatusCallback callback = new Session.StatusCallback() {  
//        @Override  
//        public void call(Session session, SessionState state,  
//                Exception exception) {  
//            onSessionStateChange(session, state, exception);  
//        }  
//    }; 
 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main, container, false);
        authButton = (LoginButton) view.findViewById(R.id.login_button);
       // authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("email","user_likes", "user_status"));
        //authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes", "user_about_me, publish_actions"));
        
        return view;
    }
    
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
	            Log.i(TAG, "Logined...");
	            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	 
					@Override
	                public void onCompleted(GraphUser user, Response response) {
	                    if (user != null) {
	                    	Log.i(TAG, "---------------------------------------------------");
	                    	Log.i(TAG, "---------------------a------------------------"+user.getId());
	                    	userInfoTextView.setText("Hello, " +user.getName());
	                    	username=user.getName();
	                    	userid=user.getId();
	                    	loginPreferences = getSharedPreferences("loginPrefs",MODE_PRIVATE);
	            		    loginPreferences.edit()
	            		    .putString("name", user.getName())
	            		    .putString("fb_id",user.getId())
	            		    .commit();		
	            		    
	            		    new CreateNewProduct().execute();
	            		    
	     
	            		    
	            		    
	            		    
	                    	//authButton.setVisibility(View.INVISIBLE);
	                    	//userInfoTextView.setText("Hello, " + user.getId()+"////"+user.getName()+"////"+user.getLink());

	                    }
	                }
				
	            });
	        } else if (state.isClosed()) {
	            Log.i(TAG, "Logged out...");
	            MainActivity.this.finish();
	            
	        }
			
		}
	};
    
    
    
    
    
    
    
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {  
//		if (state.isOpened()) {
//        Log.i(TAG, "Logined...");
//        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//
//			@Override
//            public void onCompleted(GraphUser user, Response response) {
//                if (user != null) {
//                	Log.i(TAG, "---------------------------------------------------");
//                	Log.i(TAG, "---------------------a------------------------"+user.getId());
//                	userInfoTextView.setText("Hello, " +user.getName());
//                	username=user.getName();
//                	userid=user.getId();
//                	loginPreferences = getSharedPreferences("loginPrefs",MODE_PRIVATE);
//        		    loginPreferences.edit()
//        		    .putString("name", user.getName())
//        		    .putString("fb_id",user.getId())
//        		    .commit();		
//        		    
//        		    new CreateNewProduct().execute();
//        		    
// 
//        		    
//        		    
//        		    
//                	//authButton.setVisibility(View.INVISIBLE);
//                	//userInfoTextView.setText("Hello, " + user.getId()+"////"+user.getName()+"////"+user.getLink());
//
//                }
//            }
//		
//        });
//    } else if (state.isClosed()) {
//        Log.i(TAG, "Logged out...");
//        MainActivity.this.finish();
//        
//    } 
//    }
//    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			pDialog = new ProgressDialog(Name.this);
//			pDialog.setMessage("建立使用者中...");
//			pDialog.setIndeterminate(false);
//			pDialog.setCancelable(true);
//			pDialog.show();
//		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			//String name = pName.getText().toString();
			
			Log.i(TAG, "-------------------------b--------------------");
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("fb_id", userid));
			params.add(new BasicNameValuePair("name", username));

			
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product, "POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MainPage.class);
			startActivity(intent);
			MainActivity.this.finish();
			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					
					
					// closing this screen
					finish();
				} else {
					Log.i(TAG, "no~~~~~~~~~~~~~~~");
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}

			return null;
		}
	}
	
	
	
	
 

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, uiHelper.toString());
    }
 
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "---------------c----------------------");
////		Session session = Session.getActiveSession();
////		if (session != null && session.isOpened()) {
////            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
////				@Override
////                public void onCompleted(GraphUser user, Response response) {
////                    if (user != null) {
////                    	Log.i(TAG, "++++++++++++++++++++++++++++++++++");
////    					Intent intent = new Intent();
////    					intent.setClass(MainActivity.this, MainPage.class);
////    					startActivity(intent);
////                    }
////                }
////            });
////		}
//		uiHelper.onResume();
//		buttonsEnabled(Session.getActiveSession().isOpened());
//		Session session = Session.getActiveSession();  
//        if (session != null && (session.isOpened() || session.isClosed())) {  
//            onSessionStateChange(session, session.getState(), null);  
//        }  
  
        uiHelper.onResume();  
	}
	


 
    @Override
    public void onPause() {
    	Log.i(TAG, "--------------------d----------------------");
        super.onPause();
        uiHelper.onPause();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
 
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Log.i(TAG, "------------------e-----------------------------");
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
 
//    private String buildUserInfoDisplay(GraphUser user) {
//        StringBuilder userInfo = new StringBuilder("");
// 
//        // Below data
//        // - no special permissions required
//        userInfo.append(String.format("Name: %s\n\n",
//                user.getName()));
// 
//        userInfo.append(String.format("Birthday: %s\n\n",
//                user.getBirthday()));
// 
//        userInfo.append(String.format("Location: %s\n\n",
//                user.getLocation().getProperty("name")));
// 
//        userInfo.append(String.format("Locale: %s\n\n",
//                user.getProperty("locale")));
// 
//        // Example: access via key for array (languages)
//        // - requires user_likes permission
//        JSONArray languages = (JSONArray)user.getProperty("languages");
//        if (languages.length() > 0) {
//            ArrayList languageNames = new ArrayList ();
//            for (int i=0; i < languages.length(); i++) {
//                JSONObject language = languages.optJSONObject(i);
//                // Add the language name to a list. Use JSON
//                // methods to get access to the name field.
//                languageNames.add(language.optString("name"));
//            }
//            userInfo.append(String.format("Languages: %s\n\n",
//                    languageNames.toString()));
//        }
// 
//        return userInfo.toString();
//    }
 
}




















    
