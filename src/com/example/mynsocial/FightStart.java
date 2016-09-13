package com.example.mynsocial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FightStart extends Activity implements OnClickListener{
	ImageButton go;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fightstart);
		go=(ImageButton) findViewById(R.id.go_btn);
		go.setOnClickListener(this);
	}
	
	public void onClick(View v){
		Intent intent = new Intent();
		intent.setClass(FightStart.this, BluetoothChat.class);
		startActivity(intent);
	}

}
