package com.example.mynsocial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class Result extends Activity implements OnClickListener{
	Button back;
	String fbcaption, finalscore;
	private static final String TAG = "Result";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		LinearLayout layout=(LinearLayout) findViewById(R.id.background);
		Bundle a =getIntent().getExtras();
		fbcaption = a.getString("fbcaption");//遊戲結果
		finalscore=a.getString("score");//雙方比數

		if(fbcaption.equals("I win the game!")){
			layout.setBackgroundDrawable( getResources().getDrawable(R.drawable.win) );
		}else if(fbcaption.equals("I lose the game!")){
			layout.setBackgroundDrawable( getResources().getDrawable(R.drawable.lose) );
		}else if(fbcaption.equals("We deuce!")){
			layout.setBackgroundDrawable( getResources().getDrawable(R.drawable.even) );
		}
		
		back=(Button) findViewById(R.id.backbutton);
		back.setOnClickListener(this);
		publishFeedDialog();
	}
	

	
	
    private void publishFeedDialog() {
        Bundle params = new Bundle();
        params.putString("name", "MyNSocial");
        params.putString("caption", finalscore+"!!!"+fbcaption);
        params.putString("description", "come here and play");
        params.putString("link", "http://www.tm.ncku.edu.tw/index.php");
        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
        
        // Invoke the dialog
    	WebDialog feedDialog = (
    			new WebDialog.FeedDialogBuilder(this,
    					Session.getActiveSession(),
    					params))
    					.setOnCompleteListener(new OnCompleteListener() {

    						@Override
    						public void onComplete(Bundle values,
    								FacebookException error) {
    							if (error == null) {
    								// When the story is posted, echo the success
    				                // and the post Id.
    								final String postId = values.getString("post_id");
        							if (postId != null) {
//        								Toast.makeText(this,
//        										"Posted story, id: "+postId,
//        										Toast.LENGTH_SHORT).show();
        							} else {
        								// User clicked the Cancel button
//        								Toast.makeText(this.getApplicationContext(), 
//        		                                "Publish cancelled", 
//        		                                Toast.LENGTH_SHORT).show();
        							}
    							} else if (error instanceof FacebookOperationCanceledException) {
    								// User clicked the "x" button
//    								Toast.makeText(getActivity().getApplicationContext(), 
//    		                                "Publish cancelled", 
//    		                                Toast.LENGTH_SHORT).show();
    							} else {
    								// Generic, ex: network error
//    								Toast.makeText(getActivity().getApplicationContext(), 
//    		                                "Error posting story", 
//    		                                Toast.LENGTH_SHORT).show();
    							}
    						}
    						
    						})
    					.build();
    	
    		feedDialog.show();
//    		Intent intent = new Intent();
//    		intent.setClass(BluetoothChat.this, MainPage.class);
//    		startActivity(intent);
    	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(Result.this, MainPage.class);
		startActivity(intent); 
	}

}
