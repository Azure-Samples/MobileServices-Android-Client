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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class TodosFetchService extends IntentService {
	// Status Constants
	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_FINISHED = 0x2;
	public static final int STATUS_SUCCESS = 0x3;
	public static final int STATUS_ERROR = 0x4;
	// Command Constants
	public static final int PERFORM_SERVICE_ACTIVITY = 0x5;

	public static final String COMMAND_KEY = "service_command";
	public static final String RECEIVER_KEY = "serivce_receiver";
	public static final String SERVICE_WAS_SUCCESS_KEY = "service_was_success";

	private ResultReceiver mReceiver;

	public TodosFetchService() {
		super("TodosFetchService");
	}

	public TodosFetchService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.mReceiver = intent.getParcelableExtra(RECEIVER_KEY);
		int command = intent.getIntExtra(COMMAND_KEY, PERFORM_SERVICE_ACTIVITY);
		if (this.mReceiver != null)
			this.mReceiver.send(STATUS_RUNNING, Bundle.EMPTY);
		switch (command) {
		case PERFORM_SERVICE_ACTIVITY:
			fetchTodos(intent);
			break;
		default:
			if (this.mReceiver != null)
				mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
		}
		this.stopSelf();
	}

	private void fetchTodos(Intent intent) {
		boolean fetchFailed = false;
		JSONObject[] todos = null;//
		try {
			URL url = new URL(Constants.kGetTodosUrl);

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection
					.addRequestProperty("Content-Type", "application/json");
			urlConnection.addRequestProperty("ACCEPT", "application/json");
			urlConnection.addRequestProperty("X-ZUMO-APPLICATION",
					Constants.kMobileServiceAppId);

			try {
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				BufferedReader bufferReader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder stringBuilderResponse = new StringBuilder();
				String line;
				while ((line = bufferReader.readLine()) != null) {
					stringBuilderResponse.append(line);
				}
				JSONArray jsonArray = new JSONArray(
						stringBuilderResponse.toString());
				todos = new JSONObject[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					todos[i] = jsonArray.getJSONObject(i);
				}
			} catch (Exception ex) {
				Log.e("TodosFetchService", "Error getting JSON from Server: "+ ex.getMessage());
				fetchFailed = true;
			} finally {
				urlConnection.disconnect();
			}
		} catch (Exception ex) {
			Log.e("TodosFetchService", "Error opening HTTP Connection: " + ex.getMessage());
			fetchFailed = true;
		}
		// Provided a result receiver was sent in, send a response back
		if (mReceiver != null) {
			if (fetchFailed) { 
				mReceiver.send(STATUS_ERROR, Bundle.EMPTY);
				this.stopSelf();
				mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
			} else {
				Bundle bundle = new Bundle();
				bundle.putBoolean(SERVICE_WAS_SUCCESS_KEY, true);
				bundle.putSerializable("todos", todos);
				mReceiver.send(STATUS_SUCCESS, bundle);
				this.stopSelf();
				mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
			}
		} else {
			this.stopSelf();
		}
	}
}
