package com.zoop.checkout.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zoop.checkout.app.API.PaymentsRestClient;
import com.zoop.checkout.app.Model.AssociateToken;
import com.zoop.checkout.app.Model.Buyer;
import com.zoop.checkout.app.Model.Card;
import com.zoop.checkout.app.Model.JsonTransactionNotPresent;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CardNotPresentConfirmationFragment extends FragmentLoading {
	WalletActivity walletActivity;
	View v;
    View formPlan;
    public void setActivity(WalletActivity walletActivity){
		this.walletActivity=walletActivity;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 v = inflater.inflate(R.layout.payment_confirmation_fragment, container, false);
		return v;
	}
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        Button btnback=(Button)v.findViewById(R.id.buttonback);
		Button btnconfirm=(Button)v.findViewById(R.id.buttonConfirmTransaction);
		btnback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putString("status", "canceled");
			    CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_confirmation", bundle);
                walletActivity.pager.setCurrentItem(2);

            }
		});
		btnconfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("status", "success");
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_confirmation", bundle);
                showProgress(true, formPlan, "Autorizando...");
                ChargeCardNotPresent chargeCardNotPresent = new ChargeCardNotPresent();
                chargeCardNotPresent.execute((Void) null);
			}
		});
	}
	public void getConfirmation(){
        TextView tvValue = (TextView) v.findViewById(R.id.sale_value);
        TextView tvInstallment = (TextView) v.findViewById(R.id.sale_installment);
        LinearLayout lnInstallment = (LinearLayout) v.findViewById(R.id.linear_sale_value_installment);
        TextView tvbrand = (TextView) v.findViewById(R.id.card_brand);
        ImageView imgbrand = (ImageView) v.findViewById(R.id.brand_logo);
        TextView tvValidity = (TextView) v.findViewById(R.id.card_validity);
        TextView tvholder = (TextView) v.findViewById(R.id.card_holder);
        TextView tvCardNumber = (TextView) v.findViewById(R.id.card_number);
        formPlan=(View)v.findViewById(R.id.formPlan);
        if(AssociateToken.getInstance().getInstallmentOptions()>0) {
            if (AssociateToken.getInstance().getInstallmentOptions() > 1) {
                tvInstallment.setText(AssociateToken.getInstance().getInstallmentOptionstext());
            } else {
                lnInstallment.setVisibility(View.GONE);
            }
            Float value= Float.valueOf(AssociateToken.getInstance().getValue());
            value=value/100;
            tvValue.setText("R$"+Extras.getInstance().formatBigDecimalAsLocalString(new BigDecimal((value))));
        }
        if(Card.getInstance().getNumcard()!=null) {
            tvbrand.setText(Card.getInstance().getCardBrand());
            imgbrand.setImageResource(Card.getInstance().getImgCard_brand());
            tvValidity.setText(Card.getInstance().getExpirationMonth() + "/" + Card.getInstance().getExpirationMYear());
            tvholder.setText(Card.getInstance().getHolder_name());
            String sCardNumber = Card.getInstance().getNumcard();
            String sCardNumberFormat = "**** **** **** " + sCardNumber.substring(sCardNumber.length() - 4);
            tvCardNumber.setText(sCardNumberFormat);
        }

    }
    public class ChargeCardNotPresent extends AsyncTask<Void, Void, JSONObject> {
        String id;
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                JSONObject joBuyer;
                String publishableKey =  APIParameters.getInstance().getStringParameter("publishableKey");
                String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                String seller_id = APIParameters.getInstance().getStringParameter("sellerId");
                String sUrlBuyer = "https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/buyers";
                Map<String, String> joParamsBuyer = new HashMap<>();
                Buyer buyer = Buyer.getInstance();
                AssociateToken associateToken = AssociateToken.getInstance();
                if(associateToken.getCustomer()==null) {
                    joParamsBuyer.put("first_name", buyer.getFirst_name());
                    joParamsBuyer.put("last_name", buyer.getLast_name());
                    joParamsBuyer.put("taxpayer_id", buyer.getTaxpayer_id());
                    joParamsBuyer.put("description", buyer.getDescription());
                    joParamsBuyer.put("email", buyer.getEmail());
                    joParamsBuyer.put("address[line1]", buyer.getAddress());
                    joParamsBuyer.put("address[line2]", buyer.getAddress_number());
                    joParamsBuyer.put("address[line3]", buyer.getAddress_complement());
                    joParamsBuyer.put("address[neighborhood]", buyer.getNeighborhood());
                    joParamsBuyer.put("address[city]", buyer.getCity());
                    if(!buyer.getState().equals("")) {
                        joParamsBuyer.put("address[state]", buyer.getState());
                    }
                    joParamsBuyer.put("address[postal_code]", buyer.getPostal_code());
                    if(!buyer.getCountry_code().equals("")) {
                        joParamsBuyer.put("address[country_code]", buyer.getCountry_code());
                    }
                    joBuyer = ZoopSessionsPayments.getInstance().syncPost(sUrlBuyer, publishableKey, null, getActivity(), joParamsBuyer);
                    if (joBuyer.has("id")) {
                        associateToken.setCustomer(joBuyer.getString("id"));
                    }
                }
                Card card = Card.getInstance();
                Map<String, String>  joParamsToken = new HashMap<>();
                joParamsToken.put("holder_name", card.getHolder_name());
                joParamsToken.put("expiration_month", String.valueOf(card.getExpirationMonth()));
                joParamsToken.put("expiration_year", String.valueOf(card.getExpirationMYear()));
                joParamsToken.put("security_code", card.getCVCcard());
                joParamsToken.put("card_number", card.getNumcard());
                String sURLToken = ("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/cards/tokens");
                JSONObject joToken = ZoopSessionsPayments.getInstance().syncPost(sURLToken, publishableKey, null, getActivity(), joParamsToken);
                if (joToken.has("id")) {
                    associateToken.setToken(joToken.getString("id"));
                }
                if(associateToken.getCustomer()!=null && associateToken.getToken()!=null) {
                    Map<String, String>  joParamsAssociateToken = new HashMap<>();
                    joParamsAssociateToken.put("token", associateToken.getToken());
                    joParamsAssociateToken.put("customer", associateToken.getCustomer());
                    String sURLAssociateToken = ("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/cards");
                    JSONObject joAssociateToken = ZoopSessionsPayments.getInstance().syncPost(sURLAssociateToken, publishableKey,null, getActivity(), joParamsAssociateToken);
                    JSONObject joParamsTransaction =  new JSONObject();
                    joParamsTransaction.put("customer", associateToken.getCustomer());
                    joParamsTransaction.put("amount", String.valueOf(associateToken.getValue()));
                    joParamsTransaction.put("currency", "BRL");
                    if(AssociateToken.getInstance().getInstallmentOptions()>1){
                        JSONObject joInstallment=new JSONObject();
                        joInstallment.put("mode","interest_free");
                        joInstallment.put("number_installments",AssociateToken.getInstance().getInstallmentOptions());
                        joParamsTransaction.put("installment_plan", joInstallment);
                    }

                    joParamsTransaction.put("payment_type", "credit");
                    joParamsTransaction.put("on_behalf_of", seller_id);

                    return joParamsTransaction;
                }else {
                    return null;
                }
            } catch (Exception e) {
                L.e("Error validating network login", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(final JSONObject joParamsTransaction) {
            if (joParamsTransaction != null) {
                try {
                    String publishableKey =  APIParameters.getInstance().getStringParameter("publishableKey");
                    String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                    String sURLTransaction = "/marketplaces/"+sMarketplaceId+"/transactions";
                    PaymentsRestClient paymentsRestClient = new PaymentsRestClient(publishableKey + ":");
                    StringEntity entity  = new StringEntity(joParamsTransaction.toString());
                    paymentsRestClient.post(getActivity(), sURLTransaction, entity, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            JsonTransactionNotPresent.getInstance().setJoTransaction(response.toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("status", "success");
                            CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_transaction", bundle);
                            Toast.makeText(walletActivity, "Pagamento Efetuado.", Toast.LENGTH_LONG).show();

                            showProgress(false, formPlan, "Autorizando...");
                            walletActivity.cardNotPresentSuccessfulFragment.setStatusTransaction(true);
                            walletActivity.pager.setCurrentItem(4);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Bundle bundle = new Bundle();
                            bundle.putString("status", "failed");
                            CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_transaction", bundle);
                            showProgress(false, formPlan, "Autorizando...");
                            walletActivity.cardNotPresentSuccessfulFragment.setStatusTransaction(false);
                            walletActivity.pager.setCurrentItem(4);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("status", "failed");
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_transaction", bundle);
                showProgress(false, formPlan, "Autorizando...");
                walletActivity.cardNotPresentSuccessfulFragment.setStatusTransaction(false);
                walletActivity.pager.setCurrentItem(4);
            }
        }
    }
}