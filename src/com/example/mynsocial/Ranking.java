package com.example.mynsocial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class Ranking extends ListActivity{
	
	// Progress Dialog
		private ProgressDialog pDialog;

		// Creating JSON Parser object
		JSONParser jParser = new JSONParser();

		ArrayList<HashMap<String, String>> IDList;

		// url to get all products list
		private static String url_all_products = "http://140.116.54.216/MyNSocial/get_all_products.php";

		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_ID = "fb_id";
		private static final String TAG_NAME = "name";
		private static final String TAG_WIN = "win";

		// ID JSONArray
		JSONArray ID = null;
		
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_rank);

			// Hashmap for ListView
			IDList = new ArrayList<HashMap<String, String>>();

			// Loading ID in Background Thread
			new LoadAllID().execute();

			// Get listview
			ListView lv = getListView();
		}
		
		
		/**
		 * Background Async Task to Load all ID by making HTTP Request
		 * */
		class LoadAllID extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(Ranking.this);
				pDialog.setMessage("Åª¨ú¤¤...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}

			/**
			 * getting All ID from url
			 * */
			protected String doInBackground(String... args) {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
				
				// Check your log cat for JSON reponse
				Log.d("Al l Products: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// ID found
						// Getting Array of ID
						ID = json.getJSONArray(TAG_ID);

						// looping through All ID
						for (int i = 0; i < 3; i++) {
							JSONObject c = ID.getJSONObject(i);

							// Storing each json item in variable							
							String name = c.getString(TAG_NAME);
							String win = c.getString(TAG_WIN);

							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							map.put(TAG_NAME, name);
							map.put(TAG_WIN, win);

							// adding HashList to ArrayList
							IDList.add(map);
						}
					} else {
						// no ID found
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * After completing background task Dismiss the progress dialog
			 * **/
			protected void onPostExecute(String file_url) {
				// dismiss the dialog after getting all ID
				pDialog.dismiss();
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						ListAdapter adapter = new SimpleAdapter(Ranking.this, IDList,R.layout.list_item, new String[] {TAG_NAME,TAG_WIN},
						new int[] { R.id.name, R.id.win });
						// updating listview
						setListAdapter(adapter);
					}
				});

			}

		}
		
}

