package com.zoop.checkout.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.zoop.zoopandroidsdk.ZoopTerminalPayment;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FlipperPaymentOptions extends ZoopFlipperPane {
	
	int iSelectedPaymentType = -1;
	int paymentOption[];
	LinearLayout v[];
	BigDecimal valueToCharge = null;

	
	public FlipperPaymentOptions(BigDecimal pValueToCharge) {
		super();
		valueToCharge = pValueToCharge;
	}
	
	public int getLayoutResourceId() {
		return R.layout.flipper_payment_options;
	}

	/*
	public void setChargeValue(BigDecimal pValueToCharge) {
		valueToCharge = pValueToCharge;
	}
	*/

	@Override
	public void onFlip() {
			((Button) getCurrentActivity().findViewById(R.id.buttonNext)).setEnabled(false);
			ViewPaymentOption viewPaymentOptionDebit = (ViewPaymentOption) getCurrentActivity().findViewById(R.id.layoutPaymentDebit);
			viewPaymentOptionDebit.setPaymentOption(ZoopTerminalPayment.CHARGE_TYPE_DEBIT);
			final ViewPaymentOption viewPaymentOptionCredit = (ViewPaymentOption) getCurrentActivity().findViewById(R.id.layoutPaymentCredit);
			viewPaymentOptionCredit.setPaymentOption(ZoopTerminalPayment.CHARGE_TYPE_CREDIT);
			ViewPaymentOption viewPaymentOptionCreditWithInstallments = (ViewPaymentOption) getCurrentActivity().findViewById(R.id.layoutPaymentCreditWithInstallments);
			viewPaymentOptionCreditWithInstallments.setPaymentOption(ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS);
			if (false == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true)) {
				viewPaymentOptionCreditWithInstallments.setVisibility(View.GONE);
			}
			if (false == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showCredit, true)) {
				viewPaymentOptionCredit.setVisibility(View.GONE);
			}
			if (false == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showDebit, true)) {
				viewPaymentOptionDebit.setVisibility(View.GONE);
			}
			v = new LinearLayout[3];
			v[0] = viewPaymentOptionDebit;
			v[1] = viewPaymentOptionCredit;
			v[2] = viewPaymentOptionCreditWithInstallments;
		final Boolean bPlan=APIParameters.getInstance().getBooleanParameter("enablePlan",true);

		paymentOption = new int[3];
			paymentOption[0] = ZoopTerminalPayment.CHARGE_TYPE_DEBIT;
			paymentOption[1] = ZoopTerminalPayment.CHARGE_TYPE_CREDIT;
			paymentOption[2] = ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS;
			Resources resources = getCurrentActivity().getResources();
			BigDecimal minimumValuePerInstallment = APIParameters.getInstance().getBigDecimalParameter(APISettingsConstants.Payment_MinimumInstallmentAmount);
			String commentForPaymentTypeCreditWithInstallments = null;
			// Multiply by 2 because: Payment in 2 installments of mininum value (5+5) must be equal or less than total value (10)
			if (valueToCharge.compareTo(minimumValuePerInstallment.multiply(new BigDecimal(2))) >= 0) {
				commentForPaymentTypeCreditWithInstallments = resources.getString(R.string.label_credit_with_installments_comment);
				viewPaymentOptionCreditWithInstallments.setEnabled(true);
			} else {
				viewPaymentOptionCreditWithInstallments.setEnabled(false);
				v[2] = null;
				commentForPaymentTypeCreditWithInstallments = resources.getString(R.string.credit_with_installments_less_than_minimum);
				commentForPaymentTypeCreditWithInstallments = commentForPaymentTypeCreditWithInstallments.replace("[minimum_installment_amount]", com.zoop.zoopandroidsdk.commons.Extras.formatBigDecimalAsMoneyString(minimumValuePerInstallment));
			}
			String sValueToCharge = ((TextView) getCurrentActivity().findViewById(R.id.editTextValueToCharge)).getText().toString();
			final BigDecimal valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(sValueToCharge);
		try {
			if(bPlan) {
				viewPaymentOptionDebit.setInfoPlan(Extras.getInstance().getPlanInfo("debit", valueToCharge, 1));
				viewPaymentOptionCredit.setInfoPlan(Extras.getInstance().getPlanInfo("credit", valueToCharge, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			Button info = (Button) viewPaymentOptionCredit.findViewById(R.id.bInfoPlan);
		info.setVisibility(View.GONE);
			info.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						CheckoutApplication.getFirebaseAnalytics().logEvent("payment_method_info", null);
						callDialog(Extras.getInstance().getPlanInfo("credit", valueToCharge, 1), viewPaymentOptionCredit);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			Button infoDebit = (Button) viewPaymentOptionDebit.findViewById(R.id.bInfoPlan);
		infoDebit.setVisibility(View.GONE);

			infoDebit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						CheckoutApplication.getFirebaseAnalytics().logEvent("payment_method_info", null);
						callDialog(Extras.getInstance().getPlanInfo("debit", valueToCharge, 1), viewPaymentOptionCredit);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});


			viewPaymentOptionCreditWithInstallments.setComment(commentForPaymentTypeCreditWithInstallments);


			for (int i = 0; i < v.length; i++) {
				final int iTemp = i;
				if (null != v[i]) {
					v[i].setOnClickListener(new View.OnClickListener() {
						int chargeTypeIndex = iTemp;

						@Override
						public void onClick(View v) {
							resetPaymentOptionsButtonColorToUnselected();


							((ChargeActivity) getCurrentActivity()).setSelectedPaymentOption(FlipperPaymentOptions.this.paymentOption[chargeTypeIndex]);
							//resetPaymentTypesSelection();

							//v.setBackgroundColor(getCurrentActivity().getResources().getColor(R.color.zcolor_regular_button));
							ViewPaymentOption viewPaymentOption = (ViewPaymentOption) v;
							viewPaymentOption.setSelected(true);

							//((TextView) v.findViewWithTag("title")).setTextColor(getCurrentActivity().getResources().getColor(R.color.zcolor_button_font_color));
							//					((Button) getCurrentActivity().findViewById(R.id.buttonNext)).setEnabled(true);

							((Button) getCurrentActivity().findViewById(R.id.buttonNext)).setEnabled(true);
							if (paymentOption[chargeTypeIndex] == ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS) {
								((Button) getCurrentActivity().findViewById(R.id.buttonNext)).setText(getCurrentActivity().getResources().getString(R.string.label_next));
							} else {
								((Button) getCurrentActivity().findViewById(R.id.buttonNext)).setText(getCurrentActivity().getResources().getString(R.string.label_charge));
							}
						}
					});
				}
			}


	}

	public void callDialog(String infoPlan,View v){

		FragmentManager fragmentManager = getCurrentActivity().getFragmentManager();

		DialogChangePlan dialog = new DialogChangePlan();
		dialog.setPositionBasedOnView(v);
		dialog.setPlanInfo(infoPlan);


		dialog.show(fragmentManager, "dialog");


	}
	
	void resetPaymentOptionsButtonColorToUnselected() {
		for (int i=0; i<paymentOption.length; i++) {
			if (null != v[i]) {
				((ViewPaymentOption) v[i]).setSelected(false);				
			}
			// Hide label
			//((TextView) findViewById(R.id.textViewSelectPaymentType)).setVisibility(View.GONE);
			// Change background and font color
//			v[i].setBackgroundColor(getCurrentActivity().getResources().getColor(R.color.zcolor_button_font_color));
//			((TextView) v[i].findViewWithTag("title")).setTextColor(getCurrentActivity().getResources().getColor(R.color.zcolor_regular_button));
		}
	}


	/*
	public void resetPaymentTypesSelection(){
		for (int i=0; i<viewsPaymentOptions.length; i++) {
			viewsPaymentOptions[i].setBackgroundColor(getResources().getColor(R.color.zcolor_button_font_color));
			((TextView) viewsPaymentOptions[i].findViewWithTag("title")).setTextColor(getResources().getColor(R.color.zcolor_regular_button));
		}
	}
	*/
}
