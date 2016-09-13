package com.example.mynsocial;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainPage extends Activity implements OnClickListener{//¥Dµe­±
	ImageView fight_btn, tool_btn, game_btn,setup_btn,userpicture;
	Button rank_btn;
	TextView t1;
	private static final String TAG = "BluetoothChat";
	String fb_id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mainpage);
		fight_btn=(ImageView) findViewById(R.id.flight_btn);
		tool_btn=(ImageView) findViewById(R.id.tool_btn);
		game_btn=(ImageView) findViewById(R.id.game_btn);
		setup_btn=(ImageView) findViewById(R.id.setup_btn);
		fight_btn.setOnClickListener(this);
		tool_btn.setOnClickListener(this);
		game_btn.setOnClickListener(this);
		setup_btn.setOnClickListener(this);
//        SharedPreferences sp = this.getSharedPreferences("loginPrefs",MODE_PRIVATE);
//		fb_id = sp.getString("fb_id", fb_id);
//		Log.d(TAG, "++setupChat()++"+fb_id);
//		ImageView user_picture;
//		userpicture=(ImageView)findViewById(R.id.imageView1);
//		URL img_value = null;
//		img_value = new URL("http://graph.facebook.com/"+fb_id+"/picture?type=large");
//		Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
//		userpicture.setImageBitmap(mIcon1);
		
		
		
//		URL img_value = null;
//        try {
//           img_value = new URL("http://graph.facebook.com/"+fb_id+"/picture");
//       } catch (MalformedURLException e) {
//           // TODO Auto-generated catch block
//           e.printStackTrace();
//       }
//        Bitmap mIcon1 = null;
//       try {
//           mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
//       } catch (IOException e) {
//           // TODO Auto-generated catch block
//           e.printStackTrace();
//       }
//        userpicture.setImageBitmap(mIcon1);
//		
		
		
		
		
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		t1 = (TextView)findViewById(R.id.titlename);
		if(v.getId()==R.id.flight_btn){
			Intent intent = new Intent();
			intent.setClass(MainPage.this, FightStart.class);
			startActivity(intent); 
			//MainPage.this.finish(); 
		}else if(v.getId()==R.id.tool_btn){
			Intent intent = new Intent();
			intent.setClass(MainPage.this, ToolPage.class);
			startActivity(intent); 
			//MainPage.this.finish();
		}else if(v.getId()==R.id.game_btn){
			Intent intent = new Intent();
			intent.setClass(MainPage.this, Ranking.class);
			startActivity(intent); 
			//MainPage.this.finish();
		}else if(v.getId()==R.id.setup_btn){
			Intent intent = new Intent();
			intent.setClass(MainPage.this, PersonalData.class);
			startActivity(intent); 
			//MainPage.this.finish();
		}
	}
	
	

}
