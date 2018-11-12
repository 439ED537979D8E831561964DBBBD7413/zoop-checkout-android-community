package com.zoop.checkout.app;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.uncopt.android.widget.text.justify.JustifiedEditText;
import com.uncopt.android.widget.text.justify.JustifiedTextView;
import com.zoop.zoopandroidsdk.commons.ZLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class FlipperWelcomePlan extends ZoopFlipperPane {
	int paymentOption;
	public int getLayoutResourceId() {
		return R.layout.welcome_plan_pane;
	}
	@Override
	public void onFlip() {
		String planName = "";
		try {
			 planName=Extras.getInstance().getNamePlan();
		}catch (Exception e){

		}
			String questionPlan = "Atualmente o seu plano selecionado é o " + planName + ". Veja abaixo o resumo do " + planName + ":\n\n";
			questionPlan+=planInfo()  +"\n\nCaso queira mudar para outro plano, clique no link abaixo ou próximo para continuar. Você pode trocar de planos posteriormente clicando em Configurações -> Planos.\n";
			TextView Textpair = (TextView) getCurrentActivity().findViewById(R.id.textDoc);
			Textpair.setText(Html.fromHtml("<u>Mudar Plano.</u><br><br>"));
			Textpair.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ZLog.t(300300);
					Intent intent = new Intent(getCurrentActivity(), PlansActivity.class);
					getCurrentActivity().startActivity(intent);
				}
			});
		JustifiedTextView dvText = (JustifiedTextView) getCurrentActivity().findViewById(R.id.dvText);
			dvText.setText(questionPlan);
	}
	public String planInfo() {
		String planInfo =null;
		try {
			JSONObject jotypePlan=APIParametersCheckout.getInstance().getPlanSubscription();
			JSONArray joFee_details = APIParametersCheckout.getInstance().getPlanSubscription().getJSONArray("fee_details");
			String percent_amount_debit_dialog = "";
			String percent_amount_credit_dialog = "";
			BigDecimal taxaddDebit = new BigDecimal(0);
			BigDecimal taxaddCredit = new BigDecimal(0);
			for (int j = 0; j < joFee_details.length(); j++) {
				JSONObject jObject = ((JSONObject) joFee_details.get(j));
				if(joFee_details.getJSONObject(j).getString("payment_type").equals("debit")) {
					BigDecimal tax=new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
					taxaddDebit=taxaddDebit.add(tax);
					percent_amount_debit_dialog = String.valueOf(taxaddDebit);
				}else if((joFee_details.getJSONObject(j).getString("payment_type").equals("credit")) && (joFee_details.getJSONObject(j).getString("number_installments").equals("1"))){
					BigDecimal tax=new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
					taxaddCredit=taxaddCredit.add(tax);
					percent_amount_credit_dialog= String.valueOf(taxaddCredit);
				}
			}
			if (jotypePlan.getString("name").equals("Plano Pro")||jotypePlan.getString("name").equals("Plano Top")) {
				planInfo = percent_amount_debit_dialog+ "% no débito;\n" + percent_amount_credit_dialog + "% no crédito à vista;\n" +
						"4.99% no crédito + 2.39% por parcela;\nEx: Transação parcelada em 3x:Taxa=4.99%+2.39%+2.39% \n" +
						"\n" + (jotypePlan.getString("description"));
			} else if (jotypePlan.getString("name").equals("Plano Standard")) {
				planInfo = percent_amount_debit_dialog+ "% no débito;\n" + percent_amount_credit_dialog + "% no crédito à vista;\n" +
						"4.39% no crédito de 2 a 6;\n4.69% no crédito de 7 a 12;\n\n" + (jotypePlan.getString("description"));
			}else {
				planInfo = percent_amount_debit_dialog+ "% no débito;\n" + percent_amount_credit_dialog + "% no crédito à vista;\n" +
						"\n\n" + (jotypePlan.getString("description"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return planInfo;

	}
}
