package com.zoop.checkout.app;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.uncopt.android.widget.text.justify.JustifiedTextView;
import com.zoop.zoopandroidsdk.commons.APIParameters;

public class FlipperWelcomeEnableTransaction extends ZoopFlipperPane {

	int paymentOption;
	
	public int getLayoutResourceId() {
		return R.layout.welcome_transaction_ok_pane;
	}
	

	@Override
	public void onFlip() {

		String zoopInfo;

		if (!APIParameters.getInstance().getBooleanParameter("seller_activate")) {

			zoopInfo = "Sua conta ainda não está ativa. Aguarde enquanto validamos os seus dados para ativar a sua conta. Fique atento para e-mails e notificações do Zoop sobre a sua conta.";

		}else {
			String supportEmail = "suporte@pagzoop.com";
			if (ApplicationConfiguration.SUPPORT_EMAIL != null) {
				supportEmail = ApplicationConfiguration.SUPPORT_EMAIL;
			}

			 zoopInfo = getCurrentActivity().getResources().getString(R.string.product_name) +" está pronto para ser usado. Você já pode cobrar os seus clientes e vender aceitando cartões com toda segurança!\n" +
					 "Lembre-se de ligar a sua maquininha para realizar uma venda.\n" +
					 "Em caso de dúvidas, entre em contato com o nosso suporte através do e-mail " + supportEmail +  "\n" +
					 "\nBem vindo ao "+getCurrentActivity().getResources().getString(R.string.product_name) ;
		}



		JustifiedTextView dvText = (JustifiedTextView) getCurrentActivity().findViewById(R.id.dvText);
		dvText.setText(zoopInfo);


	}
}
