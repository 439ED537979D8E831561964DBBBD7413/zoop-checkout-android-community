package com.zoop.checkout.app;

import android.webkit.WebView.FindListener;

public class FlipperChargeStatus extends ZoopFlipperPane {

	int paymentOption;
	
	public int getLayoutResourceId() {
		return R.layout.flipper_charge_status;
	}
	
	public void setSelectedPaymentOption(int pPaymentOption) {
		paymentOption = pPaymentOption;
	}
	
	@Override
	public void onFlip() {
		ViewPaymentOption viewPaymentOptionSelected = (ViewPaymentOption) getCurrentActivity().findViewById(R.id.viewPaymentOptionSelected);
		viewPaymentOptionSelected.setPaymentOption(paymentOption);
		viewPaymentOptionSelected.setSelected(true);
	}
}
