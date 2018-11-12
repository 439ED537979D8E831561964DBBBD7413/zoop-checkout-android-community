package com.zoop.checkout.app;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.checkout.app.Model.SellerSelected;
import com.zoop.zoopandroidsdk.commons.*;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class FlipperSelectSeller extends FlipperLoading {
	int position;
	EditText eOrder;
	JSONArray jaSeller;
	AutoCompleteTextView selectSeller;
	View formPlan;

	public int getLayoutResourceId() {
		return R.layout.flipper_selected_seller;
	}
	@Override
	public void onFlip() {
		formPlan=(View)getCurrentActivity().findViewById(R.id.formPlan);
		showProgress(true, formPlan, "Carregando Estabelecimentos");
		ListSeller listSeller=new ListSeller();
		listSeller.execute();

	}



	public class ListSeller extends AsyncTask<Void, Void, Boolean> {
		JSONObject joResponse = null;
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//xx
				String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
				String sSellerId = APIParameters.getInstance().getStringParameter("sellerId");
				String publishableKey = APIParameters.getInstance().getStringParameter("publishableKey");
				Long tUpdateSeller=APIParameters.getInstance().getLongParameter("tUpdateSeller",0);

				String sURL = "https://api.zoop.ws/v1/marketplaces/"+ sMarketplaceId+"/sellers?date_range[gt]="+tUpdateSeller;
				joResponse = ZoopSessionsPayments.getInstance().syncGet(sURL, publishableKey, getCurrentActivity());
				return true;

			}catch (ZoopSessionHTTPJSONResponseException zhe) {
				ZLog.exception(300009, zhe);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return false;
		}



		protected void onPostExecute(Boolean result) {
			try {
					showProgress(false, formPlan, "Carregando Estabelecimentos");
				if(joResponse!=null) {
					jaSeller = joResponse.getJSONArray("items");
					for (int i = 0; i < jaSeller.length(); i++) {
						String nameSeller;
						String sellerId = jaSeller.getJSONObject(i).getString("id");
						if (jaSeller.getJSONObject(i).getString("type").equals("business")) {
							nameSeller = jaSeller.getJSONObject(i).getString("business_name");
						} else {
							nameSeller = jaSeller.getJSONObject(i).getString("first_name") + " " + jaSeller.getJSONObject(i).getString("last_name");
						}
						if(nameSeller.equals("")||nameSeller==null){
							nameSeller="Estabelecimento Cadastrado Zoop";
						}
						//APIParameters.getInstance().putGlobalStringParameter("subSellerSelected_" + sellerId, sellerId + ";;" + nameSeller, jaSeller.getJSONObject(i).getString("created_at"));
					}
				}
					//String [] sellerInfo=APIParameters.getInstance().getParameterNameByString("subSellerSelected",false);
				String [] sellerInfo="teste".split("a");

				ArrayList<SellerSelected> lSeller=new ArrayList<>();
					for (int i=0;i<sellerInfo.length;i++){
						SellerSelected hashSeller = new SellerSelected();
						String []sellerIdName=APIParameters.getInstance().getStringParameter(sellerInfo[i]).split(";;");
						hashSeller.setId(sellerIdName[0]);
						hashSeller.setName(sellerIdName[1]);
						lSeller.add(hashSeller);
					}
					if(joResponse!=null) {
						if (jaSeller.length() > 0) {
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
							dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
							Date parsedDate = dateFormat.parse(jaSeller.getJSONObject(0).getString("created_at"));
							APIParameters.getInstance().putLongParameter("tUpdateSeller", (parsedDate.getTime()) / 1000);
						}
					}
					final TextView tSeller = (TextView) getCurrentActivity().findViewById(R.id.tSeller);
					TextView tOrder = (TextView) getCurrentActivity().findViewById(R.id.tOrder);
					tSeller.setText(APIParameters.getInstance().getStringParameter("tSeller", "Selecione o estabelecimento:"));
					tOrder.setText(APIParameters.getInstance().getStringParameter("tOrder", "Digite o Número do pedido:"));
					SellerSelectedAdapter adapter = new SellerSelectedAdapter(getCurrentActivity()
							, lSeller);
					selectSeller = (AutoCompleteTextView)
							getCurrentActivity().findViewById(R.id.selectSeller);
					selectSeller.setAdapter(adapter);
					selectSeller.setThreshold(0);
					eOrder=(EditText)getCurrentActivity().findViewById(R.id.eOrder);
					selectSeller.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus)
								selectSeller.showDropDown();
						}
					});
					selectSeller.setOnTouchListener(new View.OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							selectSeller.showDropDown();
							return false;
						}
					});
					selectSeller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view, int p, long rowId) {
							position=p;
							try {
								String sellerSelected=((TextView)view.findViewById(R.id.lSellerId)).getText().toString();
                                String sellerNameSelected=((TextView)view.findViewById(R.id.lSellerName)).getText().toString();
							//	((ChargeActivity) getCurrentActivity()).setOrderSellerIdSelected(sellerSelected,sellerNameSelected);
								InputMethodManager imm = (InputMethodManager) getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(selectSeller.getWindowToken(), 0);
							}catch (Exception e){

							}
						}
					});


				}catch(Exception e){
					ZLog.t(e.toString());
				}

		}
	}
}

