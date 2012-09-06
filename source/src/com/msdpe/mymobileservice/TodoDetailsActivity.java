// ---------------------------------------------------------------------------------- 
// Microsoft Developer & Platform Evangelism 
//  
// Copyright (c) Microsoft Corporation. All rights reserved. 
//  
// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,  
// EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES  
// OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. 
// ---------------------------------------------------------------------------------- 
// The example companies, organizations, products, domain names, 
// e-mail addresses, logos, people, places, and events depicted 
// herein are fictitious.  No association with any real company, 
// organization, product, domain name, email address, logo, person, 
// places, or events is intended or should be inferred. 
// ----------------------------------------------------------------------------------

package com.msdpe.mymobileservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class TodoDetailsActivity extends Activity {
	
	private Button mBtnSaveTodo;
	private Button mBtnMarkTodoComplete;
	private TextView mLblTodoText;
	private EditText mTxtTodoText;
	private boolean mIsAddingNewTodo;
	private String mTodoText;
	private int mTodoId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_details);
		// Get references to controls on layout
		mBtnSaveTodo = (Button) findViewById(R.id.btnSaveTodo);
		mBtnMarkTodoComplete = (Button) findViewById(R.id.btnMarkTodoComplete);
		mLblTodoText = (TextView) findViewById(R.id.lblTodoText);
		mTxtTodoText = (EditText) findViewById(R.id.txtTodoText);
		// Get extra data from intent
		Intent intent = getIntent();
		mIsAddingNewTodo = intent.getBooleanExtra("AddingNewTodo", false);
		if (mIsAddingNewTodo) {
			mBtnMarkTodoComplete.setVisibility(View.GONE);
		} else {
			mBtnSaveTodo.setVisibility(View.GONE);
			mTodoText = intent.getStringExtra("TodoText");
			mTodoId = intent.getIntExtra("TodoId", 0);
			mTxtTodoText.setText(mTodoText);
			mTxtTodoText.setFocusable(false);
		}

		mBtnMarkTodoComplete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				markTodoComplete();
			}
		});
		mBtnSaveTodo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveTodo();
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_todo_details, menu);
        return true;
    }
    
    protected void saveTodo() {
		new SaveTodoTask(this).execute(mTxtTodoText.getText().toString());
	}

	private class SaveTodoTask extends AsyncTask<String, Void, String> {

		private Activity mContext;

		public SaveTodoTask(Activity activity) {
			mContext = activity;
		}

		@Override
		protected String doInBackground(String... params) {
			String todoText = params[0];

			JSONObject jsonUrl = new JSONObject();
			try {
				jsonUrl.put("complete", "false");
				jsonUrl.put("text", todoText);
			} catch (JSONException e) {
				Log.e("TodoDetailsActivity",
						"Error creating JSON object: " + e.getMessage());
			}
			Log.i("TodoDetailsActivity", "JSON: " + jsonUrl.toString());

			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(Constants.kAddTodoUrl);
				urlConnection = (HttpURLConnection) url//
						.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.addRequestProperty("Content-Type",
						"application/json");
				urlConnection.addRequestProperty("ACCEPT", "application/json");
				urlConnection.addRequestProperty("X-ZUMO-APPLICATION",
						Constants.kMobileServiceAppId);
				// Write JSON to Server
				DataOutputStream wr = new DataOutputStream(
						urlConnection.getOutputStream());
				wr.writeBytes(jsonUrl.toString());
				wr.flush();
				wr.close();
				// Get response code
				int response = urlConnection.getResponseCode();
				// Read response
				InputStream inputStream = new BufferedInputStream(
						urlConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				StringBuilder stringBuilderResult = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilderResult.append(line);
				}
				//A successful response will have a 201 response code
				if (response == 201)
					return "SUCCESS";
				return "FAIL";

			} catch (IOException e) {
				Log.e("TodoDetailsActivity", "IO Exeception: " + e.getMessage());
				e.printStackTrace();
				return "IOERROR";
			} finally {
				urlConnection.disconnect();
			}
		}

		@Override
		protected void onPostExecute(String status) {
			// Do something with result
			if (status.equals("SUCCESS")) {
				Toast.makeText(getApplicationContext(),
						"Todo Created Successfully", Toast.LENGTH_SHORT).show();
				mContext.finishActivity(1);
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"There was an error creating the Todo: " + status,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	protected void markTodoComplete() {
		new MarkTodoCompleteTask(this).execute(mTodoId, mTodoText);
	}

	private class MarkTodoCompleteTask extends AsyncTask<Object, Void, String> {

		private Activity mContext;

		public MarkTodoCompleteTask(Activity activity) {
			mContext = activity;
		}

		@Override
		protected String doInBackground(Object... params) {
			int todoId = (Integer) params[0];
			String todoText = (String) params[1];
			try {
				JSONObject jsonUrl = new JSONObject();
				jsonUrl.put("complete", "true");
				jsonUrl.put("text", todoText);
				jsonUrl.put("id", todoId);
				HttpClient httpClient = new DefaultHttpClient();
				HttpPatch httpPatch = new HttpPatch(Constants.kUpdateTodoUrl + todoId);
				httpPatch.addHeader("Content-Type", "application/json");
				httpPatch.addHeader("ACCEPT", "application/json");
				httpPatch.addHeader("X-ZUMO-APPLICATION", Constants.kMobileServiceAppId);
				StringEntity body = new StringEntity(jsonUrl.toString());
				body.setContentType("application/json");
				httpPatch.setEntity(body);
				org.apache.http.HttpResponse response = httpClient
						.execute(httpPatch);
				//A successful response will have a 200 response code
				if (response.getStatusLine().getStatusCode() == 200)
					return "SUCCESS";
				else
					return response.getStatusLine().getStatusCode() + "";
			} catch (Exception ex) {
				return ex.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String status) {
			if (status.equals("SUCCESS")) {
				Toast.makeText(getApplicationContext(),
						"Todo updated Successfully", Toast.LENGTH_SHORT).show();
				mContext.finishActivity(1);
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"There was an error updating the Todo: " + status,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

    
}
