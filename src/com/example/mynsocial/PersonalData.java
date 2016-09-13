package com.example.mynsocial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PersonalData extends Activity{
	 public static final String KEY = "com.example.mynsocial";
	 SharedPreferences spref;
	 EditText comname,job,name,phone,address,email;
	 String scomname,sjob,sname,sphone,saddress,semail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_detail);
		comname=(EditText) findViewById(R.id.edit_companyname);
		job=(EditText) findViewById(R.id.edit_job);
		name=(EditText) findViewById(R.id.edit_name);
		phone=(EditText) findViewById(R.id.edit_phone);
		address=(EditText) findViewById(R.id.edit_address);
		email=(EditText) findViewById(R.id.edit_email);
		spref = getApplication().getSharedPreferences(KEY, Context.MODE_PRIVATE);
		scomname=spref.getString("comname", null);
		sjob=spref.getString("job", null);
		sname=spref.getString("name", null);
		sphone=spref.getString("phone", null);
		saddress=spref.getString("address", null);
		semail=spref.getString("email", null);
		comname.setText(scomname);
		job.setText(sjob);
		name.setText(sname);
		phone.setText(sphone);
		address.setText(saddress);
		email.setText(semail);
	}
	public void onclick(View v){
		SharedPreferences.Editor editor = spref.edit();
		editor.putString("comname", comname.getText().toString());
		editor.putString("job", job.getText().toString());
		editor.putString("name", name.getText().toString());
		editor.putString("phone", phone.getText().toString());
		editor.putString("address", address.getText().toString());
		editor.putString("email", email.getText().toString());	
		editor.commit();
		
		Intent intent = new Intent();
		intent.setClass(PersonalData.this, MainPage.class);
		startActivity(intent); 
		PersonalData.this.finish();

	}

}
