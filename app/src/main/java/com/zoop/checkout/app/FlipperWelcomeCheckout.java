package com.zoop.checkout.app;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.uncopt.android.widget.text.justify.JustifiedTextView;
import com.zoop.zoopandroidsdk.commons.APIParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class FlipperWelcomeCheckout extends ZoopFlipperPane {

	int paymentOption;
	
	public int getLayoutResourceId() {
		return R.layout.welcome_pane;
	}
	

	@Override
	public void onFlip() {

		String docInfo="";

		APIParameters ap = APIParameters.getInstance();

		JSONObject joSeller=APIParametersCheckout.getInstance().getSeller();
		String type="";
		try {
			 type=joSeller.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/*if (!APIParameters.getInstance().getBooleanParameter("seller_activate",true)) {
			JSONArray jaDocuments = new JSONArray();
			try {
				String documents = APIParameters.getInstance().getStringParameter("joDocuments");
				JSONObject joDocuments = new JSONObject(documents);
				JSONObject Documents = joDocuments.getJSONObject("content");
				Iterator x = Documents.keys();
				while (x.hasNext()){
					String key = (String) x.next();
					if (type.equals("individual")) {
						if (!key.equals("cnpj")) {
							JSONObject json = new JSONObject();
							json.put("documents_type", key);
							json.put("documents", Documents.get(key));
							jaDocuments.put(json);
						}
					}else{
						JSONObject json = new JSONObject();
						json.put("documents_type", key);
						json.put("documents", Documents.get(key));
						jaDocuments.put(json);
					}
				}
				for(int position=0;position<jaDocuments.length();position++) {
					JSONArray jaDo = jaDocuments.getJSONObject(position).getJSONArray("documents");
					String sNameDocument = "";
					String sNameDocumentInfo = "";
						if (jaDocuments.getJSONObject(position).getString("documents_type").equals("identificacao")) {
							sNameDocument = "Documento de identificação";
						} else if (jaDocuments.getJSONObject(position).getString("documents_type").equals("residencia")) {
							sNameDocument = "Comprovante de residência";
						} else if (jaDocuments.getJSONObject(position).getString("documents_type").equals("atividade")) {
							sNameDocument = "Comprovante de atividade";
						} else if (jaDocuments.getJSONObject(position).getString("documents_type").equals("cnpj")) {
							sNameDocument = "CNPJ";
						}
						String statusDoc;
						if (jaDo.length() == 0) {
							statusDoc = "Não enviado";
						} else {
							statusDoc = "Aprovado";
							for (int i = 0; i < jaDo.length(); i++) {
								if (jaDo.getJSONObject(i).getString("status").equals("reproved")) {
									statusDoc = "Reprovado";
								}
							}
							for (int i = 0; i < jaDo.length(); i++) {
								if (jaDo.getJSONObject(i).getString("status").equals("pending")) {

									statusDoc = "Em análise";
								}
							}
						}
						if (!statusDoc.equals("Aprovado")) {
							docInfo += " \n- " + sNameDocument + " está com o status " + statusDoc + ".";
						}
					}
			} catch (Exception e) {

			}
		}*/
		boolean Tablet = Extras.isTablet(getCurrentActivity());
		// ToDo: @mainente use Extras.getDeviceType method
		String deviceType = Extras.getDeviceTypeString(getCurrentActivity());
		String NameUser = APIParameters.getInstance().getStringParameter("firstname");
		String myData = "Ola " + NameUser + ", bem vindo(a) ao "+getCurrentActivity().getResources().getString(R.string.product_name) +
				", que permite que você cobre os seus clientes e veja as vendas efetuadas.\n\n" +
				"Para usar o "+getCurrentActivity().getResources().getString(R.string.product_name) +" você precisa revisar as informações básicas e configurar a maquininha de cartões com esse "+ deviceType  +"."
				;
	/*	if (!APIParameters.getInstance().getBooleanParameter("seller_activate",true)) {
			myData += "\nSua conta não está ativa devido as seguintes pendência: "+docInfo +"\nCaso queira enviar algum documento, clique em \"Enviar Documentos\" abaixo.";
			myData += "\nSua conta ainda não está ativa.";

			TextView textpair = (TextView) getCurrentActivity().findViewById(R.id.textDoc);
			textpair.setVisibility(View.GONE);
			textpair.setText(Html.fromHtml("<br><u>Enviar documentos.</u><br>"));
			textpair.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ZLog.t(300300);
					Intent intent = new Intent(getCurrentActivity(), DocumentsActivity.class);
					getCurrentActivity().startActivity(intent);
				}
			});
		}*/
		String myData2="Para prosseguir com o assistente de configuração, clique no botão \"Próximo\"." ;
		JustifiedTextView dvText = (JustifiedTextView) getCurrentActivity().findViewById(R.id.dvText);
		dvText.setText(myData);
		JustifiedTextView dvText2 = (JustifiedTextView) getCurrentActivity().findViewById(R.id.dvText2);
		dvText2.setText(myData2);

	}
}
