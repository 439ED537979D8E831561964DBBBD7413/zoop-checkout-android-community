package com.zoop.checkout.app;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;


import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;


public class PaymentsListActivity extends ZCLActivityWithHomeButton implements DatePickerDialog.OnDateSetListener {

    private ListView mLvPicasa;
    private View mLoginStatusView;
	View mLoginFormView;
	View mLoginStatusMessageView; 
	long _lCurrentDayToFilter;
    FetchTransactionsListForDayTask fetchTransactionsListForDayTask;
    private String sDateStringSuffix;
    public static TransactionsAdapter transactionsAdapter = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	transactionsAdapter = null;
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_payments_list);

        mLvPicasa = (ListView) findViewById(R.id.listViewPaymentsList);
		mLoginFormView = findViewById(R.id.linearLayoutPaymentsList);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
	/*	ImageView ivTransactionsHistory = (ImageView) findViewById(R.id.imageViewTransactionsHistory);
		ivTransactionsHistory.setVisibility(View.GONE);*/

        
/*        final Calendar c = Calendar.getInstance();
        c.get(Calendar.DAY_OF_MONTH);
        Calendar cal = Calendar.getInstance().(TimeZone.getDefault()); 
        cal.set(c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0); 
        long lDayToFilter = cal.getTimeInMillis();
*/
/*		Calendar now = 	Calendar.getInstance(TimeZone.getDefault());
		Calendar cal = Calendar.getInstance();
		cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		_lCurrentDayToFilter = cal.getTimeInMillis();
        
        fillTransactionsList(_lCurrentDayToFilter);
*/        
		
        Button buttonSelectDate = (Button) findViewById(R.id.buttonSelectDate);
        buttonSelectDate.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundColor(getResources().getColor(R.color.zcolor_payments_list_selected_date_button));
		        ((Button) findViewById(R.id.buttonTodaysTransactions)).setBackgroundColor(getResources().getColor(R.color.zcolor_payments_list_unselected_date_button));

		        Calendar cSelectedDate = Calendar.getInstance(TimeZone.getDefault());
				cSelectedDate.setTimeInMillis(_lCurrentDayToFilter);
		        
				DatePickerDialog dlg = new DatePickerDialog(PaymentsListActivity.this, (DatePickerDialog.OnDateSetListener) PaymentsListActivity.this, 
						cSelectedDate.get(Calendar.YEAR), cSelectedDate.get(Calendar.MONTH), cSelectedDate.get(Calendar.DAY_OF_MONTH));			
				dlg.show();
			};
			
		});
        
        
        Button buttonTodaysTransactions = (Button) findViewById(R.id.buttonTodaysTransactions);
        buttonTodaysTransactions.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundColor(getResources().getColor(R.color.zcolor_payments_list_selected_date_button));
		        ((Button) findViewById(R.id.buttonSelectDate)).setBackgroundColor(getResources().getColor(R.color.zcolor_payments_list_unselected_date_button));
				
				setTransactionsListForToday(v);
			};
			
		});
        
        ((Button) findViewById(R.id.buttonTodaysTransactions)).setBackgroundColor(getResources().getColor(R.color.zcolor_payments_list_selected_date_button));
        ((Button) findViewById(R.id.buttonSelectDate)).setBackgroundColor(getResources().getColor(R.color.zcolor_payments_list_unselected_date_button));
		setTransactionsListForToday((View) findViewById(R.id.buttonTodaysTransactions));
                
    }

    public static void invalidateTransactionsListResultSet() {
    	if (null != transactionsAdapter)
    		transactionsAdapter.notifyDataSetChanged();
    }

/*    @Override
    protected void onResume() {
        super.onResume();

        if (!mHasData && !mInError) {
            loadPage();
        }
    }

    private void showErrorDialog() {
        mInError = true;
        
        AlertDialog.Builder b = new AlertDialog.Builder(PaymentsListActivity.this);
        b.setMessage("Error occured");
    }
*/    
    
    @Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
		_lCurrentDayToFilter = c.getTimeInMillis();
				
//L.d("onDateSet: "+dayOfMonth+"/"+monthOfYear+"/"+year);
		// Start again with new array for today
		
		sDateStringSuffix = getResources().getString(R.string.label_payments_list_transactions_date_suffix_variable).replace(
									"[date]", Extras.getFormattedDateForUTCTimestamp(_lCurrentDayToFilter) );

		fillTransactionsList(_lCurrentDayToFilter);
        
	}
    
    public void fillTransactionsList(Long lDayToFilter) {
    	showProgress(true);
    	String sLoadingTransactionsForDate = getResources().getString(R.string.label_wait_loading_transactions);

		Date transactionDate = new Date(lDayToFilter);
		String sTransactionDate = Extras.getFormattedDate(transactionDate, getResources().getString(R.string.label_loading_transactions_date_format));
		sLoadingTransactionsForDate = sLoadingTransactionsForDate.replace("[date]", sTransactionDate);
    	
    	((TextView) findViewById(R.id.loading_transactions_status_message)).setText(sLoadingTransactionsForDate);
    	
    	fetchTransactionsListForDayTask = new FetchTransactionsListForDayTask();
    	fetchTransactionsListForDayTask.lDayToFilter = lDayToFilter;
    	fetchTransactionsListForDayTask.execute((Void) null);    	
    }
    
	public class FetchTransactionsListForDayTask extends AsyncTask<Void, Void, Boolean> {
		public long lDayToFilter;
		public JSONArray jaItemsList;
		BigDecimal dailyTotal;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
	    		long lTimestampStart = lDayToFilter/1000;  
	    		long lTimestampEnd = lTimestampStart + (60*60*24);
//	    		JSONObject joTransactionsQueryParameters = new JSONObject();
//	    		joTransactionsQueryParameters.put("limit", 10);
//	    		joTransactionsQueryParameters.put("offset", 0);
//	    		joTransactionsQueryParameters.put("status", "succeeded");
//	    		JSONObject joDateRange = new JSONObject();
//	    		joDateRange.put("gte", lTimestampStart);
//	    		joDateRange.put("lte", lTimestampEnd);
//	    		joTransactionsQueryParameters.put("date_range", joDateRange);
				String sURL = null;

				String sSellerId = APIParameters.getInstance().getStringParameter("sellerId");
				if (null == sSellerId) {
					ZLog.error(677453);
				}
	    		else {

					sURL = "https://api.zoop.ws/v1/marketplaces/"+ APIParameters.getInstance().getStringParameter("marketplaceId")+"/sellers/"+ sSellerId +"/transactions?date_range[gte]="+lTimestampStart+"&date_range[lte]="+lTimestampEnd+"&sort=time-descending";
				}
	    		//L.d("getTransactionsURL: "+sURL);
	    		sURL = UFUC.getUFU(sURL);
	    		
		    	JSONObject pjoResponse = ZoopSessionsPayments.getInstance().syncGet(sURL,  APIParameters.getInstance().getStringParameter("publishableKey"),PaymentsListActivity.this);
				jaItemsList = pjoResponse.getJSONArray("items");
				final String sNumberOfTransactions = pjoResponse.getString("query_count");

				// Sum total for the day
				dailyTotal = new BigDecimal(0);
				for (int i=0; i<jaItemsList.length(); i++) {
					JSONObject joTransaction = jaItemsList.getJSONObject(i);
					if (0 == joTransaction.getString("status").compareTo("succeeded")) {
						dailyTotal = dailyTotal.add(new BigDecimal(joTransaction.getString("amount")));
					}
				}
				
				///////////////////////// ATTENTION
				//// ATTENTION:
				// ALL UI MANIPULATION MUST RUN IN UI THREAD. 
				// UNEXPECTED RESULTS MAY HAPPEN IF NOT THERE
				// IT DEPENDS ON THREAD ACCESS AND IMPLIES IN UNPREDICTABLE BEHAVIOR 
				//////////////////////
	    		runOnUiThread(new Runnable() {
	    			@Override
	    			public void run() {
	    				((ListView) findViewById(R.id.listViewPaymentsList)).setVisibility(View.VISIBLE);
						String sLabelTransactions;
						if (0 == sNumberOfTransactions.compareTo("0")) {
							sLabelTransactions = getResources().getString(R.string.label_payments_list_transactions_no_transactions);
						}
						else if (0 == sNumberOfTransactions.compareTo("1")) {
							sLabelTransactions = getResources().getString(R.string.label_payments_list_transactions_singular); 
						}
						else {
							sLabelTransactions = getResources().getString(R.string.label_payments_list_transactions_plural);
						}
						sLabelTransactions = sLabelTransactions.replace("[records]", sNumberOfTransactions);
						sLabelTransactions += " "+sDateStringSuffix;
						//DateUtils.getRelativeDateTimeString(PaymentsListActivity.this, lDayToFilter, DateUtils.DAY_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
						// Relative date
						//tvLabelTransactions.setText(sLabelTransactions);
	    				fillPaymentsListHeader(sLabelTransactions, null, null);
	    			}
	    		});
	    		return true;
	    	}
	    	catch (Exception e) {
	    		ZLog.exception(300014, e);
	    		runOnUiThread(new Runnable() {
	    			@Override
	    			public void run() {
	    				//if (null != transactionsAdapter) {
	    				//	transactionsAdapter.notifyDataSetInvalidated();
	    				//}
	    				((ListView) findViewById(R.id.listViewPaymentsList)).setVisibility(View.GONE);
	    				fillPaymentsListHeader(null, null, getResources().getString(R.string.payments_list_could_not_retrieve_transactions));
	    			}
	    		});
	    	}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				showProgress(false);
				String sDailyTotal = Extras.getInstance().formatBigDecimalAsLocalString(dailyTotal);
				fillPaymentsListHeader(null, sDailyTotal, null);
				transactionsAdapter = new TransactionsAdapter(PaymentsListActivity.this, jaItemsList);
				mLvPicasa.setAdapter(transactionsAdapter);
			}
			else {
				showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
    
	}    
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} 
		else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	// if error message is not null, hide others and display error message
	public void fillPaymentsListHeader(String sDateMessage, String sTotal, String sErrorMessage) {
		if (null == sErrorMessage) {
			((View) findViewById(R.id.linearLayoutTransactionsListTotal)).setVisibility(View.VISIBLE);
			((View) findViewById(R.id.linearLayoutTransactionsListDataLabel)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.textViewPaymentsListErrorMessage)).setVisibility(View.GONE);
			
			if (null != sTotal) {
				((TextView) findViewById(R.id.textViewDailyTotal)).setText(sTotal);
			}
			if (null != sDateMessage) {
				TextView tvLabelTransactions = (TextView) findViewById(R.id.textViewLabelNumberOfTransactions);
				tvLabelTransactions.setText(sDateMessage);
			}
			//TextView tvLabelTransactions = (TextView) findViewById(R.id.textViewLabelNumberOfTransactions);

			//textViewDailyTotal
			//textViewLabelNumberOfTransactions
		}
		else {
			((View) findViewById(R.id.linearLayoutTransactionsListTotal)).setVisibility(View.GONE);
			((View) findViewById(R.id.linearLayoutTransactionsListDataLabel)).setVisibility(View.GONE);
			TextView textViewPaymentsListErrorMessage = (TextView) findViewById(R.id.textViewPaymentsListErrorMessage);		
			textViewPaymentsListErrorMessage.setVisibility(View.VISIBLE);
			textViewPaymentsListErrorMessage.setText(sErrorMessage);
		}
		
	}

	private void setTransactionsListForToday(View v) {
//		v.setBackgroundColor(getResources().getColor(R.color.zcolor_regular_button));
//        ((Button) findViewById(R.id.buttonSelectDate)).setBackgroundColor(getResources().getColor(R.color.zcolor_regular_button));

		Calendar cal = Calendar.getInstance();
        Calendar now = 	Calendar.getInstance(TimeZone.getDefault());
		cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		_lCurrentDayToFilter = cal.getTimeInMillis();
		now.setTimeInMillis(_lCurrentDayToFilter);		
		
		sDateStringSuffix = getResources().getString(R.string.label_payments_list_transactions_date_suffix_today);
		fillTransactionsList(_lCurrentDayToFilter);
		
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (1 == requestCode) {
			fillTransactionsList(_lCurrentDayToFilter);

		}
	}
	
}