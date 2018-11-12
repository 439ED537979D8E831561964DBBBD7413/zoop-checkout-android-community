package com.zoop.checkout.app;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ProgressBar;


public class VoidTransactionAsyncTask extends AsyncTask<Void, Void, Boolean> {

	JSONObject joTransactionResponse;
	AlertDialog.Builder alert;
	ProgressBar progressBar;
	
	public VoidTransactionAsyncTask(AlertDialog.Builder pAlert, ProgressBar pProgressBar, JSONObject pjoTransactionResponse) {
		alert = pAlert;
		joTransactionResponse = pjoTransactionResponse;
		progressBar = pProgressBar;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		alert.setMessage("HAHAHAHAHA");
		return null;
	}
	
	
	
}
