package com.zoop.checkout.app;

import java.math.BigDecimal;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;

public class FlipperKeypad extends ZoopFlipperPane {
			
    TextView tvValue = null;

	public int getLayoutResourceId() {
		return R.layout.flipper_keypad;
	}
	
	@Override
	public void onFlip() {
		tvValue = (TextView) getCurrentActivity().findViewById(R.id.editTextValueToCharge);
		tvValue.setText(Extras.getInstance().formatBigDecimalAsLocalString(new BigDecimal(0)));
		
		Button bClear = (Button) getCurrentActivity().findViewById(R.id.btClear);
		bClear.setOnClickListener( new OnClickListener() {				
			@Override
			public void onClick(View v) {				
				((ChargeActivity) getCurrentActivity()).resetPaymentOptions();
			}
		});

		ImageButton bBackspace= (ImageButton) getCurrentActivity().findViewById(R.id.btBackspace);
		bBackspace.setOnClickListener( new OnClickListener() {				
			@Override
			public void onClick(View v) {
				try { 
					BigDecimal value = Extras.getInstance().getBigDecimalFromDecimalString( tvValue.getText().toString());
					value = Extras.getInstance().divideBy10(value);
					tvValue.setText(Extras.getInstance().formatBigDecimalAsLocalString(value));
					
				}
				catch (Exception exception) {
					L.e("Error while processing Custom Value - Backspace pressed", exception);
				}				
			}
		});
		
		View parent = getCurrentActivity().findViewById(R.id.linearLayoutKeypad);
		for (int i=0; i<=9; i++) {
			// Use tags to reuse similar code
			Button bNumber = (Button) parent.findViewWithTag(Integer.toString(i));
			final int buttonNumber = i;
			bNumber.setOnClickListener( new OnClickListener() {
				int intValueOfThisButton;
				@Override
				public void onClick(View v) {
					try {
						intValueOfThisButton = buttonNumber;						
						String sValue = tvValue.getText().toString();
						tvValue.setText(Extras.getInstance().formatBigDecimalAsLocalString(Extras.addDigitToBigDecimal(sValue, intValueOfThisButton)));
					}
					catch (Exception e) {
						L.e("Error adding number from touchpad", e);
					}
				}
			});
		}

	}
}
