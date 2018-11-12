package com.zoop.checkout.app;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;


import com.zoop.zoopandroidsdk.commons.ZLog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class TransactionsAdapter extends BaseAdapter {

	Activity parentActivity;
	JSONArray jaDataList;
	int RECEIPT_INTENT=1;

	public TransactionsAdapter(Activity parentActivity, JSONArray jaDataList) {
		super();
		this.parentActivity = parentActivity;
		this.jaDataList = jaDataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = parentActivity.getLayoutInflater().inflate(R.layout.list_item_payment, null);
		}

		try {
			final JSONObject joTransaction = jaDataList.getJSONObject(position);
			JSONObject joPaymentMethod = joTransaction.getJSONObject("payment_method");

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						String sRegexAcceptedTransactionStatuses="(succeeded|canceled)";
						//String sRegexAcceptedTransactionStatuses = APIParameters.getInstance().getStringParameter(APISettingsConstants.ZoopCheckout_RegexAcceptedTransactionStatuses);
						if (joTransaction.getString("status").matches(sRegexAcceptedTransactionStatuses)) {
							Intent receiptIntent = new Intent(parentActivity, ReceiptActivity.class);
							Bundle b = new Bundle();
							b.putString("transactionJSON", joTransaction.toString());
							receiptIntent.putExtras(b); //Put your id to your next Intent
							parentActivity.startActivityForResult(receiptIntent,RECEIPT_INTENT);
						}
						else {
							Toast.makeText(v.getContext() , (String) "Recibo não disponível. Transação rejeitada", Toast.LENGTH_LONG).show();
						}
					}
					catch (Exception e) {
						ZLog.exception(300071, e);
					}
				}
			});


			//((ImageView) convertView.findViewById(R.id.imageViewVoidTransaction)).setVisibility(View.GONE);

			String sCardBrand = joPaymentMethod.getString("card_brand");
			String sTransactionType = "";
			if (0 == joTransaction.getString("payment_type").compareTo("credit")) {
				if (!joTransaction.isNull("installment_plan")) {
					sTransactionType = parentActivity.getResources().getString(R.string.label_credit_with_installments);
				}
				else {
					sTransactionType = parentActivity.getResources().getString(R.string.label_credit);
				}
			}
			else if (0 == joTransaction.getString("payment_type").compareTo("debit")) {
				sTransactionType = parentActivity.getResources().getString(R.string.label_debit);
			}

			Date dateTime = Extras.getDateFromFullZoopAPITimestampString(joTransaction.getString("created_at"));
			// ToDo: Get library to display "4 hours ago" or Today instead the date and time
//			StringBuilder sTransactionDateTime = new StringBuilder();
//			sTransactionDateTime.append(dateTime.getHours()).append(':').append(	.getMinutes());

			String sDateFormat = convertView.getContext().getResources().getString(R.string.transactions_list_transaction_datetime_format);
			SimpleDateFormat dateFormat = new SimpleDateFormat(sDateFormat, Locale.US);

			String sTransactionDateTime = dateFormat.format(dateTime);

			//String sTransactionDateTime = (String) DateUtils.getRelativeDateTimeString(parentActivity.getApplicationContext(), dateTime.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS , 0);
			//String sTransactionDateTime = Extras.getFormattedDate(dateTime, mActivity.getResources().getString(R.string.date_format));
			String sAmount = Extras.getInstance().formatBigDecimalAsLocalMoneyString(new BigDecimal(joTransaction.getDouble("amount")));
			//String sAmount = currencyFormat.format();

			convertView.setTag(joTransaction.getString("id"));

			String sInstallmentInfo = "";
			if (! joTransaction.isNull("installment_plan")) {
				sInstallmentInfo = " ("+joTransaction.getJSONObject("installment_plan").getInt("number_installments")+"x)";
			}

			TextView textViewTransactionTypeText = (TextView) convertView.findViewById(R.id.textViewPaymentListItemTransactionType);
			TextView textViewAmount = (TextView) convertView.findViewById(R.id.textViewPaymentListItemValue);

			textViewTransactionTypeText.setText(sCardBrand+' '+sTransactionType + sInstallmentInfo);
			((TextView) convertView.findViewById(R.id.textViewPaymentListItemDateTime)).setText(sTransactionDateTime);

			TextView textViewTransactionStatusLetterLabel = (TextView) convertView.findViewById(R.id.textViewTransactionStatusLetterLabel);

			if (Extras.checkIfTransactionWasCancelled(joTransaction)) {
				textViewTransactionStatusLetterLabel.setText( parentActivity.getResources().getString(R.string.label_transaction_cancelled_letter));
				textViewTransactionStatusLetterLabel.setBackgroundColor(Color.parseColor("#ff0000"));
				textViewTransactionStatusLetterLabel.setVisibility(View.VISIBLE);

				textViewTransactionTypeText.setTextColor(Color.parseColor("#a0a0a0"));
				textViewAmount.setPaintFlags(textViewTransactionTypeText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				textViewAmount.setText(sAmount);
			}
			else if (0 == joTransaction.getString("status").compareTo("failed")) {
				textViewTransactionStatusLetterLabel.setText( parentActivity.getResources().getString(R.string.label_transaction_failed_letter));
				textViewTransactionStatusLetterLabel.setBackgroundColor(Color.parseColor("#808080"));
				textViewTransactionStatusLetterLabel.setVisibility(View.VISIBLE);

				textViewTransactionTypeText.setTextColor(Color.parseColor("#b0b0b0"));
				textViewAmount.setPaintFlags(textViewTransactionTypeText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				textViewAmount.setText(sAmount);
			}
			else if (0 == joTransaction.getString("status").compareTo("succeeded")) {
				textViewTransactionStatusLetterLabel.setBackgroundColor(Color.parseColor("#ffffff"));
				textViewTransactionStatusLetterLabel.setVisibility(View.INVISIBLE);
				textViewTransactionTypeText.setTextColor(Color.parseColor("#000000"));

				textViewAmount.setPaintFlags(textViewTransactionTypeText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
				textViewAmount.setText(sAmount);
			}
			else {
				textViewTransactionStatusLetterLabel.setText("?");
				textViewTransactionStatusLetterLabel.setVisibility(View.VISIBLE);
				textViewTransactionStatusLetterLabel.setBackgroundColor(Color.parseColor("#808080"));

				textViewTransactionTypeText.setTextColor(Color.parseColor("#a0a0a0"));
				textViewAmount.setPaintFlags(textViewTransactionTypeText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				textViewAmount.setText(sAmount);
			}
			/*
			ImageView imageViewReceipt = (ImageView) convertView.findViewById(R.id.imageViewReceipt);
			imageViewReceipt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent receiptIntent = new Intent(parentActivity, ReceiptActivity.class);
					Bundle b = new Bundle();
					b.putString("transactionJSON", joTransaction.toString());
					receiptIntent.putExtras(b); //Put your id to your next Intent
					parentActivity.startActivity(receiptIntent);
				}
			});

			ImageView imageViewVoidTransaction = (ImageView) convertView.findViewById(R.id.imageViewVoidTransaction);
			if (Extras.checkIfTransactionCanBeCancelled(joTransaction.getString("created_at"))) {
				imageViewVoidTransaction.setVisibility(View.VISIBLE);
				imageViewVoidTransaction.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Extras.showVoidTransactionConfirmationDialog(TransactionsAdapter.this.parentActivity, joTransaction);
					}
				});
			}
			else {
				imageViewVoidTransaction.setVisibility(View.GONE);
			}
			*/
		} catch (Exception e) {
			ZLog.exception(300003, e);
		}
		return convertView;

	}


	@Override
	public int getCount() {
		return jaDataList.length();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


}