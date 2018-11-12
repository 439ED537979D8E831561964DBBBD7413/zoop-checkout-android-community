package com.zoop.checkout.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.zoop.checkout.app.Model.Buyer;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;
import com.zoop.zoopandroidsdk.sessions.Retrofit;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterBuyerFragment extends FragmentLoading {
	WalletActivity walletActivity;
	View v;
    EditText buyer_address_postal_code;
    EditText buyer_name;
    EditText buyer_taxpayer_id;
    EditText buyer_description;
    EditText buyer_address;
    EditText buyer_address_number;
    EditText buyer_email;
    private TextWatcher cepMask;
    EditText buyer_address_complement;
    EditText uf;
    EditText neighborhood;
    EditText city;
    EditText country;
    String sCep;
    View formPlan;

    Bundle registerUser = new Bundle();

	public void setActivity(WalletActivity walletActivity){
		this.walletActivity=walletActivity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 v = inflater.inflate(R.layout.fragment_buyer_register, container, false);
		return v;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        formPlan=(View)v.findViewById(R.id.formPlan);
        buyer_address_postal_code=(EditText)v.findViewById(R.id.buyer_address_postal_code);
        buyer_name=(EditText)v.findViewById(R.id.buyer_name);
        uf=(EditText)v.findViewById(R.id.buyer_address_state);
        neighborhood=(EditText)v.findViewById(R.id.buyer_address_neighborhood);
        city=(EditText)v.findViewById(R.id.buyer_address_city);
        country=(EditText)v.findViewById(R.id.buyer_address_country);
        buyer_taxpayer_id=(EditText) v.findViewById(R.id.buyer_taxpayer_id);
        buyer_description=(EditText) v.findViewById(R.id.buyer_description);
        buyer_address_number=(EditText) v.findViewById(R.id.buyer_address_number);
        buyer_address_complement=(EditText) v.findViewById(R.id.buyer_address_complement);
        buyer_address=(EditText) v.findViewById(R.id.buyer_address);
        buyer_email=(EditText) v.findViewById(R.id.buyer_email);
        cepMask = Mask.insert("#####-###", buyer_address_postal_code);
        buyer_address_postal_code.addTextChangedListener(cepMask);
        Button btnNext=(Button)v.findViewById(R.id.btnNext);
        Button btnPrevious=(Button)v.findViewById(R.id.btnPrevious);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int numberOfFilledFields = 0;

                boolean validation = true;
                String msgValidation="";
                if (TextUtils.isEmpty(buyer_address_postal_code.getText().toString())) {
                    validation=false;
                    msgValidation="- Informe o Cep";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setPostal_code(buyer_address_postal_code.getText().toString());
                }
                if (TextUtils.isEmpty(buyer_name.getText().toString())) {
                    validation=false;
                    msgValidation+="\n- Informe o Nome do Comprador";
                } else {
                    numberOfFilledFields++;
                    String[] name=buyer_name.getText().toString().split(" ");
                    Buyer.getInstance().setFirst_name(name[0]);
                    String lastName=buyer_name.getText().toString().replace(name[0],"");
                    Buyer.getInstance().setLast_name(lastName);
                }
                if (TextUtils.isEmpty(uf.getText().toString())) {
                    validation=false;
                    msgValidation+="\n- Informe o Estado do Comprador";
                }else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setState(uf.getText().toString());
                }
                if (TextUtils.isEmpty(neighborhood.getText().toString())) {
                    validation=false;
                    msgValidation+="\n- Informe o Bairro do Comprador";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setNeighborhood(neighborhood.getText().toString());
                }
                if (TextUtils.isEmpty(city.getText().toString())) {
                    validation=false;
                    msgValidation+=" \n- Informe a Cidade do Comprador";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setCity(city.getText().toString());
                }
                Buyer.getInstance().setCountry_code("BR");
                if (TextUtils.isEmpty(buyer_taxpayer_id.getText().toString())) {
                    validation=false;
                    msgValidation+=" \n- Informe o Cpf do Comprador";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setTaxpayer_id((buyer_taxpayer_id.getText().toString()));
                }
                if (TextUtils.isEmpty(buyer_description.getText().toString())) {
                    validation=false;
                    msgValidation+=" \n- Informe uma descrição";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setDescription((buyer_description.getText().toString()));
                }
                if (TextUtils.isEmpty(buyer_address_number.getText().toString())) {
                    validation=false;
                    msgValidation+=" \n- Informe o Número do Endereço do Comprador";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setAddress_number((buyer_address_number.getText().toString()));
                }
                if (!TextUtils.isEmpty(buyer_address_complement.getText().toString())) {
                    numberOfFilledFields++;
                    Buyer.getInstance().setAddress_number((buyer_address_complement.getText().toString()));
                }
                if (TextUtils.isEmpty(buyer_address.getText().toString())) {
                    validation=false;
                    msgValidation+=" \n- Informe o Endereço do Comprador";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setAddress((buyer_address.getText().toString()));
                }
                if (TextUtils.isEmpty(buyer_email.getText().toString())) {
                    validation=false;
                    msgValidation+=" \n- Informe o Endereço do Comprador";
                } else {
                    numberOfFilledFields++;
                    Buyer.getInstance().setEmail((buyer_email.getText().toString()));
                }

                registerUser.putString("status", "success");
                registerUser.putInt("filled_fields", numberOfFilledFields);
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_client_info", registerUser);

                Extras.hideKeyboard(getActivity());
                walletActivity.pager.setCurrentItem(3);


            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Extras.hideKeyboard(getActivity());

                int numberOfFilledFields = 0;

                if (!TextUtils.isEmpty(buyer_address_postal_code.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_name.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(uf.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(neighborhood.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(city.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_taxpayer_id.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_description.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_address_number.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_address_complement.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_address.getText().toString())) {
                    numberOfFilledFields++;
                }
                if (!TextUtils.isEmpty(buyer_email.getText().toString())) {
                    numberOfFilledFields++;
                }

                registerUser.putString("status", "canceled");
                registerUser.putInt("filled_fields", numberOfFilledFields);
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_client_info", registerUser);

                walletActivity.pager.setCurrentItem(1);
            }
        });
        try {
            buyer_address_postal_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                    } else {
                        sCep = buyer_address_postal_code.getText().toString();
                        SearchCep searchCep = new SearchCep();
                        searchCep.execute((Void) null);
                    }
                }
            });
        }catch (Exception e){

        }
	}

    public class SearchCep extends AsyncTask<Void, Void, Boolean> {
        JSONObject joAddress = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = "http://apps.widenet.com.br/busca-cep/api/cep/"+sCep+".json";
            String baseUrl="http://apps.widenet.com.br";

            try {

                joAddress = Retrofit.getInstance().syncGet(url, baseUrl, null, getActivity());


                return true;


            } catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(final Boolean result) {
            if (result) {
                try {
                    buyer_address.setText(joAddress.getString("address"));
                    neighborhood.setText(joAddress.getString("district"));
                    city.setText(joAddress.getString("city"));
                    uf.setText(joAddress.getString("state"));
                    country.setText("Brasil");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
