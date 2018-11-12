package com.zoop.checkout.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.api.ZoopAPIErrors;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;
import com.zoop.zoopandroidsdk.terminal.ApplicationDisplayListener;
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;
import com.zoop.zoopandroidsdk.terminal.TerminalMessageType;
import com.zoop.zoopandroidsdk.terminal.VoidTransactionListener;
import com.zoop.zoopandroidsdk.ZoopTerminalVoidPayment;
import com.zoop.zoopandroidsdk.commons.ZLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VoidTransactionActivity extends Activity implements DeviceSelectionListener, ApplicationDisplayListener, VoidTransactionListener {

	ZoopTerminalVoidPayment zoopTerminalVoidPayment = null;
	JSONObject joTransaction = null;
	JSONObject joVoidedTransaction = null;
	TextView tvStatusMessage = null;

	ImageView ivMessageImage = null;
	AutoResizeTextView tvMessageText = null;
	String sMarketplaceId;
	String seller_id;
	String publishableKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			//setTheme(android.R.style.Theme_Holo_Light_Dialog);
		}
		catch (Exception e) {

		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_void_transaction);
		 sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
		 seller_id = APIParameters.getInstance().getStringParameter("sellerId");
		 publishableKey  = APIParameters.getInstance().getStringParameter("publishableKey");

		ivMessageImage = (ImageView) findViewById(R.id.imageViewVoidTransactionStatus);
		tvMessageText = (AutoResizeTextView) findViewById(R.id.textViewStatusText);

		findViewById(R.id.buttonAcknowledgeVoidTransactionSuccessful).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//VoidTransactionActivity.this.finish();3771

                Intent returnIntent = new Intent();
                returnIntent.putExtra("transactionJSON",joVoidedTransaction.toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
			}
		});

		findViewById(R.id.buttonCancelVoidTransaction).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					zoopTerminalVoidPayment.requestAbortCharge();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.buttonRetryVoidTransaction).setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View view) {
				 startVoidTransaction();
			 }
		 });

		Bundle b = getIntent().getExtras();

		try {
            joTransaction = new JSONObject(b.getString("joTransaction"));

            //alert.setTitle(activityParent.getResources().getString(R.string.dialog_void_receipt_transaction_title));
            String sConfirmationMessage = getResources().getString(R.string.dialog_void_receipt_transaction_text_confirm_void);
			BigDecimal bdTransactionToVoidValue = Extras.getInstance().getBigDecimalFromDecimalStringInZoopPaymentsFormat(joTransaction.getString("amount"));
            sConfirmationMessage = sConfirmationMessage.replace("[transaction_value]", Extras.getInstance().formatBigDecimalAsLocalMoneyString(bdTransactionToVoidValue));
            sConfirmationMessage = sConfirmationMessage.replace("[masked_card_number]", joTransaction.getJSONObject("payment_method").getString("first4_digits"));

            ((TextView) findViewById(R.id.textViewTransactionToVoidDetails)).setText(sConfirmationMessage);

            Button buttonConfirmOperation = (Button) findViewById(R.id.buttonConfirmOperation);
            buttonConfirmOperation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                    	Bundle voidBundle = new Bundle();
                    	voidBundle.putString("status", "success");
						CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_void_transaction", voidBundle);
						startVoidTransaction();
                    } catch (Exception e) {

                    }
                }
            });

            //alert.setMessage(sConfirmationMessage);

            //alert.setPositiveButton("Estornar Venda", new DialogInterface.OnClickListener() {
            //	public void onClick(DialogInterface dialog, int whichButton) {
            //			VoidTransactionDialog voidTransactionDialog = new VoidTransactionDialog(activityParent, joTransactionResponse);
            //
            //			}
            //		});

            Button buttonCancelOperation = (Button) findViewById(R.id.buttonCancelOperation);
            buttonCancelOperation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
					Bundle voidBundle = new Bundle();
					voidBundle.putString("status", "canceled");
					CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_void_transaction", voidBundle);
                    finish();
                }
            });


        }
        catch (JSONException e) {
		}


	}

	public void startVoidTransaction() {
		try {
			findViewById(R.id.layoutConfirmTransaction	).setVisibility(View.GONE);
			findViewById(R.id.layoutVoidTransactionStatusPane).setVisibility(View.VISIBLE);
			VoidTransactionActivity.this.tvStatusMessage = (TextView) findViewById(R.id.textViewStatusText);
			String entry_mode;
			entry_mode=joTransaction.getJSONObject("point_of_sale").getString("entry_mode");
			findViewById(R.id.layoutVoidConfirmation).setVisibility(View.GONE);
			if(entry_mode.equals("manually_keyed")){
				VoidCardNotPresent voidCardNotPresent = new VoidCardNotPresent();
				voidCardNotPresent.execute((Void) null);

			}else {
				findViewById(R.id.buttonCancelVoidTransaction).setVisibility(View.VISIBLE);

				zoopTerminalVoidPayment = new ZoopTerminalVoidPayment();
				zoopTerminalVoidPayment.setApplicationDisplayListener(VoidTransactionActivity.this);
				zoopTerminalVoidPayment.setVoidPaymentListener(this);
				zoopTerminalVoidPayment.voidTransaction(joTransaction.getString("id"),sMarketplaceId,seller_id,publishableKey);

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void showMessage(final String message, final TerminalMessageType messageType) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvMessageText.setText(message);
				if (TerminalMessageType.WAIT_TIMEOUT_PRETIMEOUT_WARNING == messageType) {
					MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.jobro_5_beep_b);
					mPlayer.start();
				}
			}
		});
	}

	@Override
	public void showMessage(final String message, final TerminalMessageType messageType, final String sExplanationMessage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				tvMessageText.setText(message);
				if (TerminalMessageType.WAIT_TIMEOUT_PRETIMEOUT_WARNING == messageType) {
					MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.jobro_5_beep_b);
					mPlayer.start();
				}
			}
		});
	}

	@Override
	public void showDeviceListForUserSelection(Vector<JSONObject> vectorZoopTerminals) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

			}
		});

	}

	@Override
	public void updateDeviceListForUserSelecion(JSONObject joNewlyFoundZoopDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iNewlyFoundDeviceIndex) {

	}

	@Override
	public void bluetoothIsNotEnabledNotification() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.startDiscovery();

	}

	@Override
	public void deviceSelectedResult(JSONObject joZoopSelectedDevice, Vector<JSONObject> vectorAllAvailableZoopTerminals, int iSelectedDeviceIndex) {

	}

	/*
        @Override
        public void smsReceiptResult(int result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        }

        @Override
        public void emailReceiptResult(int result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        }
    */
	@Override
	public void voidTransactionSuccessful(final JSONObject pjoVoidedTransaction) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
                findViewById(R.id.buttonCancelVoidTransaction).setVisibility(View.GONE);
				findViewById(R.id.buttonAcknowledgeVoidTransactionSuccessful).setVisibility(View.VISIBLE);
                tvMessageText.setText(getResources().getString(R.string.void_transaction_succcessful));
				VoidTransactionActivity.this.joVoidedTransaction = pjoVoidedTransaction;
			}
		});

	}

	@Override
	public void voidTransactionFailed(final JSONObject joResponse) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					findViewById(R.id.buttonCancelVoidTransaction).setVisibility(View.GONE);
					findViewById(R.id.layoutConfirmTransaction).setVisibility(View.VISIBLE);
					Button buttonOK = (Button) findViewById(R.id.buttonConfirmOperation);
					buttonOK.setText(getResources().getString(R.string.void_transaction_dialog_retry_operation));
					//ToDo: Usar a mensagem amigável que veio no JSON
					if(joResponse==null){
						tvMessageText.setText("erro ao realizar estorno");
					}else {
						tvMessageText.setText(joResponse.getString("i18n_checkout_message") + " - " + joResponse.getString("i18n_checkout_message_explanation"));
					}
					}
				catch (Exception e) {

				}
			}
		});

	}


	@Override
	public void currentVoidTransactionCanBeAbortedByUser(final boolean canAbortCurrentCharge) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
                findViewById(R.id.buttonCancelVoidTransaction).setEnabled(canAbortCurrentCharge);
			}
		});

	}

	@Override
	public void voidAborted() {
		finish();
	}

	public class VoidCardNotPresent extends AsyncTask<Void, Void, Boolean> {
		JSONObject joVoid;
		@Override
		protected Boolean doInBackground(Void... params) {
			try {

				String transactionId = joTransaction.getString("id");
				String sUrlVoid = "https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/transactions/" + transactionId + "/void";
				Map<String, String> joParamsvoid = new HashMap<>();
				BigDecimal amount = new BigDecimal(joTransaction.getString("amount")).multiply(new BigDecimal(100));
				joParamsvoid.put("amount", String.valueOf(amount));
				joParamsvoid.put("on_behalf_of", seller_id);
				joVoid = ZoopSessionsPayments.getInstance().syncPost(sUrlVoid, publishableKey, null, VoidTransactionActivity.this, joParamsvoid);

				return true;
			} catch (Exception e) {
				L.e("Error validating network login", e);
			}
			return false;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				voidTransactionSuccessful(joVoid);

			} else {
				voidTransactionFailed(null);
			}
		}
	}
}
