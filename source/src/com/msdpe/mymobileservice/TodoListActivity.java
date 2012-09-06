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

import java.util.TreeSet;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TodoListActivity extends ListActivity implements
ServiceResultReceiver.Receiver {

	private ServiceResultReceiver mReceiver;
	private JSONObject[] mTodos;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new ServiceResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		startTodoFetchService();
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Convert the tapped view into a TextView
				TextView tv = (TextView) view;
				// Load the details intent for this specific slug
				Intent todoDetailsIntent = new Intent(getApplicationContext(),
						TodoDetailsActivity.class);
				todoDetailsIntent.putExtra("TodoText", tv.getText().toString());
				todoDetailsIntent.putExtra("AddingNewTodo", false);
				try {
					for (JSONObject todoItem : mTodos) {
						if (todoItem.getString("text").equals(
								tv.getText().toString())) {
							todoDetailsIntent.putExtra("TodoId", todoItem.getInt("id"));
						}
					}
				} catch (Exception ex) {
					Log.e("TodoListActivity", ex.getMessage());
				}
				startActivityForResult(todoDetailsIntent, 1);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_todo_list, menu);
        return true;
    }

	private void startTodoFetchService() {
		final Intent serviceIntent = new Intent(Intent.ACTION_SYNC, null,
				getApplicationContext(), TodosFetchService.class);
		// put the specifics for the submission service commands
		serviceIntent.putExtra(TodosFetchService.RECEIVER_KEY, mReceiver);
		serviceIntent.putExtra(TodosFetchService.COMMAND_KEY,
				TodosFetchService.PERFORM_SERVICE_ACTIVITY);
		// Start the service
		startService(serviceIntent);
	}
	
	@Override
	public void onReceiveResult(int resultCode, Bundle resultBundle) {
		switch (resultCode) {
		case TodosFetchService.STATUS_RUNNING:
			// Don't do anything, the service is running
			break;
		case TodosFetchService.STATUS_SUCCESS:
			boolean wasSuccess = resultBundle
					.getBoolean(TodosFetchService.SERVICE_WAS_SUCCESS_KEY);
			if (wasSuccess) {
				// Success, update the ListView
				mTodos = (JSONObject[]) resultBundle.getSerializable("todos");
				showTodosInListView(mTodos);
			} else {
				// Failure, show error message
				Toast.makeText(
						getApplicationContext(),
						"There was an error fetching the URL data.  Please try again later.",
						Toast.LENGTH_LONG).show();
			}
			break;
		case TodosFetchService.STATUS_FINISHED:
			break;
		case TodosFetchService.STATUS_ERROR:
			// Error returned from service, show and error message
			Toast.makeText(
					getApplicationContext(),
					"There was an error fetching the Todo data."
							+ "Please try again later.", Toast.LENGTH_LONG)
					.show();
			break;
		}
	}
	
	private void showTodosInListView(JSONObject[] mTodos2) {
		try {
			TreeSet<String> treeSetKeys = new TreeSet<String>();
			for (int i = 0; i < mTodos2.length; i++) {
				treeSetKeys.add(mTodos[i].getString("text"));
			}
			String[] keys = (String[]) treeSetKeys
					.toArray(new String[treeSetKeys.size()]);
			ArrayAdapter adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, keys);
			setListAdapter(adapter);
		} catch (Exception ex) {
			Log.e("TodoListActivity", ex.getMessage());
		}
	}
}
