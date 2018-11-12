package com.zoop.checkout.app;

import com.zoop.zoopandroidsdk.terminal.TerminalMessageType;
import org.json.JSONException;
import org.json.JSONObject;
import com.zoop.zoopandroidsdk.terminal.VoidTransactionListener;
import com.zoop.zoopandroidsdk.commons.ZLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

public class VoidTransactionDialog implements VoidTransactionListener {
	
	JSONObject joTransactionResponse;
	AlertDialog.Builder alertBuilder;
	AlertDialog alertDialog;
	ReceiptActivity activityParent;

	public VoidTransactionDialog(ReceiptActivity pActivityParent, JSONObject pjoTransactionResponse) {
		try {
			activityParent = pActivityParent;
			joTransactionResponse = pjoTransactionResponse;
			alertBuilder = new AlertDialog.Builder(activityParent);
	
			Resources resources = activityParent.getResources();
			alertBuilder.setTitle(resources.getString(R.string.void_transaction_dialog_title));
			
			//if (joTransactionResponse.)
			String sConfirmationMessage = resources.getString(R.string.void_transaction_dialog_insert_card).replace("[masked_card_number]", joTransactionResponse.getJSONObject("payment_method").getString("first4_digits"));
			alertBuilder.setMessage(sConfirmationMessage);
	
			final SmoothProgressBar progressBar = new SmoothProgressBar(activityParent.getApplicationContext());
			//progressBar.
			progressBar.setIndeterminate(true);
			alertBuilder.setView(progressBar);
								
			alertBuilder.setNegativeButton(resources.getString(R.string.void_transaction_dialog_cancel_void), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					ZLog.t(300007);
				}
			});
			
//			ChargeActivity.terminalPayment.setVoidTransactionListener(this);
//			ChargeActivity.terminalPayment.voidTransaction(joTransactionResponse.getString("id"));
	
			alertDialog = alertBuilder.create();
			alertDialog.show();
		}
		catch (Exception e) {
			ZLog.exception(300008, e);
		}
	}


	@Override
	public void voidTransactionSuccessful(final JSONObject joVoidedTransaction) {
		activityParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    joTransactionResponse.put("voided", true);
                    joTransactionResponse.put("status", "cancelled");
                    alertDialog.dismiss();
                    activityParent.notifyVoidTransactionSuccessful();
                }
                catch (JSONException e) {
                    ZLog.exception(300016, e);
                }

            }
        });
    }
/*
    public VoidTransactionDialog(ReceiptActivity pActivityParent, JSONObject pjoTransactionResponse) {
        try {
            activityParent = pActivityParent;
            joTransactionResponse = pjoTransactionResponse;
            alertBuilder = new AlertDialog.Builder(activityParent);

            Resources resources = activityParent.getResources();
            alertBuilder.setTitle(resources.getString(R.string.void_transaction_dialog_title));

            //if (joTransactionResponse.)
            String sConfirmationMessage = resources.getString(R.string.void_transaction_dialog_insert_card).replace("[masked_card_number]", joTransactionResponse.getJSONObject("payment_method").getString("first4_digits"));
            alertBuilder.setMessage(sConfirmationMessage);

            final SmoothProgressBar progressBar = new SmoothProgressBar(activityParent.getApplicationContext());
            //progressBar.
            progressBar.setIndeterminate(true);
            alertBuilder.setView(progressBar);

            alertBuilder.setNegativeButton(resources.getString(R.string.void_transaction_dialog_cancel_void), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ZLog.t(300007);
                }
            });
// ChargeActivity.terminalPayment.setVoidTransactionListener(this);
//            ChargeActivity.terminalPayment.voidTransaction(joTransactionResponse.getString("id"));

            alertDialog = alertBuilder.create();
            alertDialog.show();
        }
        catch (Exception e) {
            ZLog.exception(300008, e);
        }
    }
*/
/*
    @Override
    public void voidTransactionSuccessful(final JSONObject joVoidedTransaction) {
        activityParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    joTransactionResponse.put("voided", true);
                    joTransactionResponse.put("status", "cancelled");
                    alertDialog.dismiss();
                    activityParent.notifyVoidTransactionSuccessful();
                } catch (JSONException e) {
                    ZLog.exception(300016, e);
                }
//				alertBuilder.setMessage("Transação aprovada");
//				alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
//				alertBuilder.setPositiveButton(activityParent.getResources().getString(R.string.void_transaction_dialog_void_button_ok), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {					
//					}
//				});
            }
        });

    }
*/

    @Override
    public void voidTransactionFailed(JSONObject joResponse) {
        activityParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ZLog.error(300011, joTransactionResponse.getString("id"));
                    alertBuilder.setMessage(activityParent.getResources().getString(R.string.void_transaction_dialog_void_failed));
                }
                catch (Exception e) {
                    ZLog.exception(300012, e);
                }
            }
        });
    }


    public void showTerminalMessageInApplication(final String message, TerminalMessageType messageType) {
        activityParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertBuilder.setMessage(message);
            }
        });

    }


    @Override
    public void currentVoidTransactionCanBeAbortedByUser(
            boolean canAbortCurrentCharge) {
    }

    @Override
    public void voidAborted() {

    }
}