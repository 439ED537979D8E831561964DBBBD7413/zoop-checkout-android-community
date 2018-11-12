package com.zoop.checkout.app;

import java.math.BigDecimal;

import org.json.JSONObject;

import com.zoop.zoopandroidsdk.api.ZoopSignatureView;

import com.zoop.zoopandroidsdk.commons.ZLog;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class CaptureSignatureActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_signature);
		try {
			TextView textViewTotalCharged = (TextView) findViewById(R.id.textViewTotalCharged);
			
			Bundle b = getIntent().getExtras();
			JSONObject joTransactionResponse = new JSONObject(b.getString("transactionJSON"));

			//com.zoop.checkout.app.Extras.
	//		BigDecimal bdTotal = Extras.getInstance().getBigDecimalFromDecimalString(joTransactionResponse.getString("amount"));
	//		String sTransactionAmount = com.zoop.commons.Extras.formatBigDecimalAsMoneyString(bdTotal);
			BigDecimal value = Extras.getInstance().getBigDecimalFromDecimalStringInZoopPaymentsFormat(joTransactionResponse.getString("amount"));
			String sTransactionAmount = Extras.getInstance().formatBigDecimalAsLocalMoneyString(value);
			textViewTotalCharged.setText(sTransactionAmount);
	        
	        String sMaskedCardNumber = joTransactionResponse.getJSONObject("payment_method").getString("first4_digits");
	        //sMaskedCardNumber = com.zoop.commons.Extras.rPadChar(sMarkedCardNumber, '*', 16);
	        TextView textViewMaskedCardNumber = (TextView) findViewById(R.id.textViewMaskedCardNumber);
	        textViewMaskedCardNumber.setText(sMaskedCardNumber);
	        
	        String sCardholderName = com.zoop.zoopandroidsdk.commons.Extras.getReceiptCardholderName(joTransactionResponse);
	        ((TextView) findViewById(R.id.textViewCardholderName)).setText(sCardholderName);
		}
		catch (Exception e) {
			ZLog.exception(300024, e);
		}
		Button buttonAcceptSignature = (Button) findViewById(R.id.buttonAcceptSignature);
		buttonAcceptSignature.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
            	ZoopSignatureView zoopSignatureView = (ZoopSignatureView) findViewById(R.id.signatureView);
				Bundle dataBundle = new Bundle();
				/**
				 * #ZoopAPICall: Uses the method getSignatureDate() from ZoopSignatureView class to retrieve the cardholder signature.
				 */
                dataBundle.putString("signatureData", zoopSignatureView.getSignatureData());
                               
                Intent intent = new Intent();
                intent.putExtras(dataBundle);
                setResult(RESULT_OK, intent);   
                finish();				
			}
		});
		
		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);   
                finish();				
			}
		});
		
		Button buttonClear = (Button) findViewById(R.id.buttonClearSignature);
		buttonClear.setOnClickListener( new OnClickListener() {		
			@Override
			public void onClick(View v) {
            	ZoopSignatureView zoopSignatureView = (ZoopSignatureView) findViewById(R.id.signatureView);
            	zoopSignatureView.clear();
			}
		});		
	}
}
