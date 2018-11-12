package com.zoop.checkout.app;

import android.app.Fragment;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.checkout.app.Model.AssociateToken;
import com.zoop.zoopandroidsdk.commons.APIParameters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static com.zoop.checkout.app.ChargeActivity.terminalPayment;

public class ChargeFragment extends Fragment {

	WalletActivity walletActivity;
	View v;



	public void setActivity(WalletActivity walletActivity){

		this.walletActivity=walletActivity;



	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 v = inflater.inflate(R.layout.charge_fragment, container, false);
		Button btnStartStep=(Button)v.findViewById(R.id.btnstart_step);
		final TextView tvValue = (TextView) v.findViewById(R.id.editTextValueToCharge);
		if(AssociateToken.getInstance().getValue()>0){
			Float value= Float.valueOf(AssociateToken.getInstance().getValue());
			value=value/100;
			tvValue.setText(Extras.getInstance().formatBigDecimalAsLocalString(new BigDecimal((value))));
			((Button) v.findViewById(R.id.btnstart_step)).setEnabled(true);

		}else {
			tvValue.setText(Extras.getInstance().formatBigDecimalAsLocalString(new BigDecimal(0)));

		}


		btnStartStep.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BigDecimal value = Extras.getInstance().getBigDecimalFromDecimalString( tvValue.getText().toString());
				value=value.multiply(new BigDecimal(100));
				BigDecimal limitValueVirtual=APIParameters.getInstance().getBigDecimalParameter("limitValueVirtual",new BigDecimal(0));

				if(value.compareTo(limitValueVirtual)<=0 || limitValueVirtual.compareTo(new BigDecimal(0))==0) {


					Spinner sInstallments = (Spinner) getActivity().findViewById(R.id.installments);
					ArrayList<String> aInstallments = new ArrayList<String>();
					BigDecimal iValue = Extras.getInstance().getBigDecimalFromDecimalString(tvValue.getText().toString());

					aInstallments.add("1x de R$" + Extras.getInstance().formatBigDecimalAsLocalString((iValue)));
					BigDecimal valueInstalment;
					for (int i = 2; i <= 12; i++) {
						BigDecimal installments = new BigDecimal(i);
						valueInstalment = iValue.divide(installments, 2, RoundingMode.HALF_UP);
						if (valueInstalment.compareTo(new BigDecimal(5)) < 0) {
							break;
						} else {


							aInstallments.add(i + "x de R$" + Extras.getInstance().formatBigDecimalAsLocalString(valueInstalment));
						}

					}
					ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, aInstallments);
					ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
					spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sInstallments.setAdapter(spinnerArrayAdapter);
				/*int compare=value.compareTo(new BigDecimal(AssociateToken.getInstance().getValue()));
				if(compare==0 ){
					sInstallments.setSelection(AssociateToken.getInstance().getInstallmentOptions()-1);
				}*/

					AssociateToken.getInstance().setValue(value.intValue());
					walletActivity.pager.setCurrentItem(1);

				}else {
					String slimitValueVirtual=Extras.getInstance().formatBigDecimalAsLocalMoneyString(limitValueVirtual.divide(new BigDecimal(100)));
					Toast.makeText(getActivity(),"O máximo habilitado para uso da maquininha virtual é de "+slimitValueVirtual+". Para alterar o limite, entre em contato com suporte@pagzoop.com",Toast.LENGTH_LONG).show();
				}


			}
		});
		Button bClear = (Button) v.findViewById(R.id.btClear);
		bClear.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {

					((TextView) getActivity().findViewById(R.id.editTextValueToCharge)).setText(Extras.getInstance().formatBigDecimalAsLocalString(new BigDecimal(0)));
						}
		});
		ImageButton bBackspace= (ImageButton) v.findViewById(R.id.btBackspace);
		bBackspace.setOnClickListener( new View.OnClickListener() {
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
		View parent = v.findViewById(R.id.linearLayoutKeypad);
		for (int i=0; i<=9; i++) {
			// Use tags to reuse similar code
			Button bNumber = (Button) v.findViewWithTag(Integer.toString(i));
			final int buttonNumber = i;
			bNumber.setOnClickListener( new View.OnClickListener() {
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
		tvValue.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				BigDecimal valueToCharge = Extras.getInstance().getBigDecimalFromDecimalString(s.toString());
				if ((valueToCharge.compareTo(terminalPayment.getMinimumChargeValue()) >= 0) && (valueToCharge.compareTo(terminalPayment.getMaximumChargeValue()) <= 0)) {
					((Button) v.findViewById(R.id.btnstart_step)).setEnabled(true);
				} else {
					((Button) v.findViewById(R.id.btnstart_step)).setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		return v;
	}



	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
