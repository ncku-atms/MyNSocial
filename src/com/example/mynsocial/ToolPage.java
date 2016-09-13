package com.example.mynsocial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class ToolPage extends Activity implements OnClickListener{
	private static final String TAG = "LoginFragment";
	Button ex_card,add_friend,do_all,msg,like;
	TextView tv;
	String userid=null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_toolpage);
		tv=(TextView) findViewById(R.id.t11);
		ex_card=(Button) findViewById(R.id.ex_card);
		ex_card.setOnClickListener(this);
		add_friend=(Button) findViewById(R.id.add_friend);
		add_friend.setOnClickListener(this);
		do_all=(Button) findViewById(R.id.doall);
		do_all.setOnClickListener(this);
		msg=(Button) findViewById(R.id.msg);
		msg.setOnClickListener(this);
		like=(Button) findViewById(R.id.likeit);
		like.setOnClickListener(this);
		//因為抓id需要時間，因此在此先抓
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
				@Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                    	userid=user.getId();
                    	Log.i(TAG, userid+"in toolpage");
                    }
                }
            });
		}
	}
	

	
	public void onClick(View v) {
		Intent intent = new Intent();
		if(v.getId()==R.id.ex_card){
			intent.setClass(ToolPage.this, NFC_Touch.class);
			Bundle bundle = new Bundle();
			bundle.putInt("vid", 1);
            intent.putExtras(bundle);
		}else if(v.getId()==R.id.add_friend){
			intent.setClass(ToolPage.this, NFC_Touch.class);
			Bundle bundle = new Bundle();
			bundle.putInt("vid", 2);
			bundle.putString("userid",userid );
            intent.putExtras(bundle);
		}else if(v.getId()==R.id.doall){
			intent.setClass(ToolPage.this, NFC_Touch.class);
			Bundle bundle = new Bundle();
			bundle.putInt("vid", 3);
			bundle.putString("userid",userid );
            intent.putExtras(bundle);
		}else if(v.getId()==R.id.msg|| v.getId()==R.id.likeit){
			intent.setClass(ToolPage.this, GamePage.class);
		}
		startActivity(intent);
	}

	
}
