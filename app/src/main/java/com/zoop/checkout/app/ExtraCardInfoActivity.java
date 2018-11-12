package com.zoop.checkout.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoop.zoopandroidsdk.ZoopTerminalPayment;

public class ExtraCardInfoActivity extends Activity {


	public static final int EXTRA_INFO_CARD_LAST_4_DIGITS = 10;
	public static final int EXTRA_INFO_CARD_EXPIRATION_DATE = 11;
	public static final int EXTRA_INFO_CARD_CVC = 12;
	
	int extraInfoToRequest;

	@Override
	public void onBackPressed() {

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_extra_card_info);

		Intent extraInfoIntent = getIntent();
		extraInfoToRequest = extraInfoIntent.getIntExtra("extraInfoToRequest", -1); // will return "FirstKeyValue"
		
		LinearLayout linearLayoutCVCSpecific = (LinearLayout) findViewById(R.id.linearLayoutCVCSpecific);
		TextView textViewLabelExtraInformation = (TextView) findViewById(R.id.textViewLabelExtraInformation);
		
		if (EXTRA_INFO_CARD_CVC == extraInfoToRequest) {	
			textViewLabelExtraInformation.setText(getResources().getString(R.string.extra_information_label_cvc));
			linearLayoutCVCSpecific.setVisibility(View.VISIBLE);
			ImageView ivCVCHelp = (ImageView) findViewById(R.id.extraCardInfoHelp);
			ivCVCHelp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intentCVCHelp = new Intent(ExtraCardInfoActivity.this, CVCHelpActivity.class);
					startActivity(intentCVCHelp);
				}
			});
		}
		else if (EXTRA_INFO_CARD_EXPIRATION_DATE  == extraInfoToRequest) {
			textViewLabelExtraInformation.setText(getResources().getString(R.string.extra_information_label_expiration_date));
			linearLayoutCVCSpecific.setVisibility(View.GONE);
		}
		else if (EXTRA_INFO_CARD_LAST_4_DIGITS == extraInfoToRequest) {
			String labelText = getResources().getString(R.string.extra_information_label_last_4_digits);
			textViewLabelExtraInformation.setText(labelText);			
			linearLayoutCVCSpecific.setVisibility(View.GONE);
		}

		Button bOK = (Button) findViewById(R.id.buttonOK);
		bOK.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				TextView textViewNumber = (TextView ) findViewById(R.id.textViewNumber);
                finishActivityOK(textViewNumber.getText().toString());
			}
		});
		
		Button buttonCVCNotReadable = (Button) findViewById(R.id.buttonCVCNotReadable);
		buttonCVCNotReadable.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finishActivityOK(ZoopTerminalPayment.CVC_NOT_READABLE);
				
			}
		});
		
		Button buttonCVCNotPresent = (Button) findViewById(R.id.buttonCVCNotPresent);
		buttonCVCNotPresent.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finishActivityOK(ZoopTerminalPayment.CVC_NOT_AVAILABLE);
			}

		});
					
		Button bClear = (Button) findViewById(R.id.btClear);
		bClear.setOnClickListener( new OnClickListener() {				
			@Override
			public void onClick(View v) {
				TextView tvValue = (TextView) findViewById(R.id.textViewNumber);
				tvValue.setText("");
			}
		});
		
		Button bBackspace= (Button) findViewById(R.id.btBackspace);
		bBackspace.setOnClickListener( new OnClickListener() {				
			@Override
			public void onClick(View v) {
				TextView tvValue = (TextView) findViewById(R.id.textViewNumber);
				String currentValue = (String) tvValue.getText();
				if (currentValue.length() > 0) {
					tvValue.setText(currentValue.substring(0, currentValue.length()-1));
				}
			}
		});
		
		View parent = findViewById(R.id.LinearLayoutExtraInfo);
		for (int i=0; i<=9; i++) {
			// Use tags to reuse similar code
			Button bNumber = (Button) parent.findViewWithTag(Integer.toString(i));
			final int buttonNumber = i;
			bNumber.setOnClickListener( new OnClickListener() {
				int intValueOfThisButton;
				@Override
				public void onClick(View v) {
					intValueOfThisButton = buttonNumber;
					TextView tvValue = (TextView) findViewById(R.id.textViewNumber);
					tvValue.setText(tvValue.getText()+Integer.toString(intValueOfThisButton));
				}
			});
		}
		
	}
	
	public void finishActivityOK(String returnValue) {
		Bundle dataBundle = new Bundle();
		TextView textViewNumber = (TextView ) findViewById(R.id.textViewNumber);
        dataBundle.putString("extraInfo", returnValue);
        Intent intent = new Intent();
        intent.putExtras(dataBundle);
        setResult(RESULT_OK, intent);
        finish();						
	}

	public void finishActivityCancel(int returnValue) {
		Bundle dataBundle = new Bundle();
		TextView textViewNumber = (TextView ) findViewById(R.id.textViewNumber);
        Intent intent = new Intent();
        intent.putExtras(dataBundle);
        setResult(RESULT_CANCELED, intent);   
        finish();						
	}

	
}
