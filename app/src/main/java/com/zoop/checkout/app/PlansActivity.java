package com.zoop.checkout.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mainente on 05/02/16.
 */
public class PlansActivity extends LoadingActivity  {

    private RecyclerView mRecyclerView;
    private PlanCardAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    View formPlan;
    JSONObject joPlan;
    JSONObject joPlanSubscription;
    final int ZOOP_POST = 2;
    final int ZOOP_GET  = 3;
    int ZOOP_DELETE  = 4;
    Spinner sgrid;
    String[] optionsGrid;

    JSONArray newJsonArray;

    ChangePlan aChangePlan;

    JSONArray jaPlan;
    PlanSubscriptions planSubscriptions;
    private Spinner sPlanSelected;
    EditText eSaleValue;
    TextView eZoopPayment;
    TextView eClientPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_type_plan);
        Intent intent = getIntent();
        if(intent.hasExtra("ChangePlanTransaction")) {
      //      getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        // Extras.getInstance().checkLogin(getApplication(),this);
        formPlan=(View)findViewById(R.id.formPlan);
        showProgress(true, formPlan, "Carregando Planos. Aguarde...");

        Button chargePlan=(Button)findViewById(R.id.chargePlan);
        mRecyclerView = (RecyclerView) findViewById(R.id.cardListPlan);
        sPlanSelected=(Spinner)findViewById(R.id.sPlanSelected);
        eZoopPayment=(TextView)findViewById(R.id.eZoopPayment);
        eClientPayment=(TextView)findViewById(R.id.eClientPayment);
        sgrid=(Spinner)findViewById(R.id.sPlanSelected);
        planSubscriptions = new PlanSubscriptions();
        planSubscriptions.execute((Void) null);
        chargePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(true, formPlan,  "Alterando Plano");
                aChangePlan = new ChangePlan();
                aChangePlan.execute((Void) null);
            }
        });

        sgrid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (!eSaleValue.getText().toString().equals("")) {


                        String tax = sgrid.getSelectedItem().toString();
                        String[] taxAmount = tax.split("\\+");


                        tax = (tax.substring(tax.indexOf("(") + 1, tax.indexOf(")"))).replace("%", "");


                        BigDecimal valuepaid = null;


                        //  valuepaid = Double.valueOf(eSaleValue.getText().toString().replace(".", "").replace("R$", "").replace(",", "."));


                        valuepaid = (Extras.getInstance().getBigDecimalFromDecimalString(eSaleValue.getText().toString().replace("R$", "").replace("$", "")));
                        BigDecimal bTax = new BigDecimal(tax);

                        BigDecimal bValueZoop = (valuepaid).multiply(bTax);
                        bValueZoop = bValueZoop.divide(new BigDecimal(100));

                        BigDecimal bValueTotal = valuepaid.subtract(bValueZoop);


                        if (taxAmount.length > 1) {
                            BigDecimal amount = Extras.getInstance().getBigDecimalFromDecimalString(taxAmount[1].replace(" ", "").replace("R$", "").replace("$", ""));
                            bValueZoop = bValueZoop.add(amount);
                            bValueTotal = bValueTotal.subtract(amount);

                        }


                        eZoopPayment.setText("R$" + Extras.getInstance().formatBigDecimalAsLocalString(bValueZoop));

                        eClientPayment.setText("R$" + Extras.getInstance().formatBigDecimalAsLocalString(bValueTotal));

                    }
                }catch (Exception e){

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eSaleValue=(EditText)findViewById(R.id.eSaleValue);
        eSaleValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        eSaleValue.addTextChangedListener(new TextWatcher() {

        private boolean isUpdating = false;
        // Pega a formatacao do sistema, se for brasil R$ se EUA US$
        private NumberFormat nf = NumberFormat.getCurrencyInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int after) {
            // Evita que o método seja executado varias vezes.
            // Se tirar ele entre em loop
            if (isUpdating) {
                isUpdating = false;
                return;
            }

            isUpdating = true;
            String str = s.toString();
            // Verifica se já existe a máscara no texto.
            boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
                    (str.indexOf(".") > -1 || str.indexOf(",") > -1));
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                        .replaceAll("[.]", "").replaceAll("[$]", "");
            }

            try {
                // Transformamos o número que está escrito no EditText em
                // monetário.
                if (!(str.equals(""))) {
                    str = nf.format(Double.parseDouble(str) / 100);
                }
                eSaleValue.setText(str);
                eSaleValue.setSelection(eSaleValue.getText().length());
                String tax = sgrid.getSelectedItem().toString();
                String[] taxAmount=tax.split("\\+");
                tax = (tax.substring(tax.indexOf("(") + 1, tax.indexOf(")"))).replace("%", "");
                BigDecimal valuepaid = null;
                if (!eSaleValue.getText().toString().equals("")) {
                    //  valuepaid = Double.valueOf(eSaleValue.getText().toString().replace(".", "").replace("R$", "").replace(",", "."));
                    valuepaid=(Extras.getInstance().getBigDecimalFromDecimalString(eSaleValue.getText().toString().replace("R$", "").replace("$", "")));
                    BigDecimal bTax=new BigDecimal(tax);

                    BigDecimal bValueZoop = (valuepaid).multiply(bTax);
                    bValueZoop=bValueZoop.divide(new BigDecimal(100));

                    BigDecimal bValueTotal = valuepaid.subtract(bValueZoop);



                    if (taxAmount.length>1){
                        BigDecimal amount=Extras.getInstance().getBigDecimalFromDecimalString(taxAmount[1].replace(" ","").replace("R$", "").replace("$", ""));
                        bValueZoop=bValueZoop.add(amount);
                        bValueTotal=bValueTotal.subtract(amount);

                    }


                    eZoopPayment.setText("R$" + Extras.getInstance().formatBigDecimalAsLocalString(bValueZoop));

                    eClientPayment.setText("R$" + Extras.getInstance().formatBigDecimalAsLocalString(bValueTotal));

                }


            } catch (NumberFormatException e) {
                s = "";
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    });
    }



    public class ChangePlan extends AsyncTask<Void, Void, Boolean> {
        private String ticket;
        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject joUser = null;


            JSONObject joResponse = null;
            try {
                String publishableKey =  APIParameters.getInstance().getStringParameter("publishableKey");
                String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                String seller_id = APIParameters.getInstance().getStringParameter("sellerId");

                String sCheckoutPublicKey= APIParameters.getInstance().getStringParameter("sCheckoutPublicKey");
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("wished_plan", APIParameters.getInstance().getStringParameter("planSelected"));
                requestParams.put("zoop_publishable_key", publishableKey);
                String planSubscriptionId=  APIParameters.getInstance().getStringParameter("planSubscriptionId");
                if(planSubscriptionId!=null) {
                    String sPlanSubscriptionChangesUrlDelete = UFUC.getUFU("https://portal.pagzoop.com/api/v1/payments_api/marketplaces/" + sMarketplaceId + "/seller/" + seller_id + "/subscriptions/" + planSubscriptionId);

                    JSONObject JoDelete;

                    //  ZoopSession.getInstance().deleteSynchronousRESTRequest(sPlanSubscriptionChangesUrlDelete, sCheckoutPublicKey, null, "laravel_session=" + cookie);
                    Map<String, String>  requestParamsDelete = new HashMap<>();
                    requestParamsDelete.put("zoop_publishable_key", publishableKey);

                    JoDelete = ZoopSessionsPayments.getInstance().syncDeleteWithCookie( sPlanSubscriptionChangesUrlDelete, sCheckoutPublicKey, requestParamsDelete, PlansActivity.this);
                }
                String sPlanSubscriptionChangesUrl = UFUC.getUFU("https://portal.pagzoop.com/api/v1/payments_api/marketplaces/" + sMarketplaceId + "/seller/" + seller_id + "/subscriptions");




                joResponse=ZoopSessionsPayments.getInstance().syncPostWithCookie(sPlanSubscriptionChangesUrl, sCheckoutPublicKey, requestParams,PlansActivity.this);
                JSONObject joPlan=APIParametersCheckout.getInstance().getPlan();
                int length=joPlan.getJSONArray("items").length();
                for(int i=0;i<length;i++){
                    if(joPlan.getJSONArray("items").getJSONObject(i).getString("id").equals(APIParameters.getInstance().getStringParameter("planSelected"))){
                        APIParameters.getInstance().putStringParameter("planSubscription", joPlan.getJSONArray("items").getJSONObject(i).toString());
                    }
                }
                APIParameters.getInstance().putStringParameter("planSubscriptionId", joResponse.getJSONObject("message").getString("resource"));
                return true;
            }catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return false;
        }
        protected void  onPostExecute(final Boolean result) {
            if (result) {
                showProgress(false, formPlan, "Alterando Plano");
                Toast.makeText(PlansActivity.this, "Plano alterado",
                        Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("old_plan",joPlanSubscription.toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(PlansActivity.this, "Erro ao alterar o plano",
                        Toast.LENGTH_LONG).show();
                showProgress(false, formPlan, "Alterando Plano");
            }
        }
    }
    public class PlanSubscriptions extends AsyncTask<Void, Void, Boolean> {
        private String ticket;
        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject joUser = null;
            JSONObject joResponse = null;
            try {

                String publishableKey =  APIParameters.getInstance().getStringParameter("publishableKey");
                String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                String seller_id = APIParameters.getInstance().getStringParameter("sellerId");
                String sPlanURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/plans");
                JSONObject joPlanApi= ZoopSessionsPayments.getInstance().syncGet(sPlanURL, publishableKey,PlansActivity.this);
                APIParameters.getInstance().putStringParameter("plan",joPlanApi.toString());
                String sPlanSubscriptionsUrl = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + seller_id + "/subscriptions");
                JSONObject joPlanSubscriptions= ZoopSessionsPayments.getInstance().syncGet(sPlanSubscriptionsUrl, publishableKey,PlansActivity.this);
                if(joPlanSubscriptions.getJSONArray("items").length()<1){
                    JSONObject joPlan=APIParametersCheckout.getInstance().getPlan();
                    int length=joPlan.getJSONArray("items").length();
                    for(int i=0;i<length;i++){
                        if(joPlan.getJSONArray("items").getJSONObject(i).getString("name").equals("Plano Standard")){
                            APIParameters.getInstance().putStringParameter("planSubscription", joPlan.getJSONArray("items").getJSONObject(i).toString());
                            APIParameters.getInstance().putStringParameter("planSubscriptionId", null);
                        }
                    }
                }else {
                    JSONObject joPlan = joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan");
                    APIParameters.getInstance().putStringParameter("planSubscription", joPlan.toString());
                    APIParameters.getInstance().putStringParameter("planSubscriptionId", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getString("id"));
                }
                return true;
            }catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return false;

        }


        protected void  onPostExecute(final Boolean result) {
            showProgress(false, formPlan, "Carregando Planos");
            if(result) {
                joPlanSubscription=APIParametersCheckout.getInstance().getPlanSubscription();
                try{
                    joPlan=APIParametersCheckout.getInstance().getPlan();
                    jaPlan=joPlan.getJSONArray("items");
                    String planSelected=joPlanSubscription.getString("id");
                    APIParameters.getInstance().putStringParameter("planSelected",planSelected );
                }catch (Exception e){
                    ZLog.exception(1, e);
                }
                mRecyclerView.setHasFixedSize(true);
                // use a linear layout manager
                mLayoutManager = new LinearLayoutManager(PlansActivity.this);
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(mLayoutManager);
             //   String idPlan = APIParameters.getInstance().getStringParameter("planosNaoDisponiveis", "pgto_instantaneo_high_volume_d0, pgto_instantaneo_high_volume_d1");
                String idPlan = APIParameters.getInstance().getStringParameter("planosDisponiveis", "pgto_standard_d30, pgto_instantaneo_d0");

                newJsonArray = new JSONArray();
                for (int i = jaPlan.length()-1; i>=0; i--) {
                    try {
                        if((idPlan.contains((jaPlan.getJSONObject(i)).getString("id")) || (idPlan.contains((jaPlan.getJSONObject(i)).getString("id"))&&(jaPlan.getJSONObject(i).getString("id").equals(joPlanSubscription.getString("id")))))) {
                            newJsonArray.put(jaPlan.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // specify an adapter (see also next example)
                mAdapter = new PlanCardAdapter(PlansActivity.this,newJsonArray,PlansActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);
                try {
                    JSONArray joFee_details= null;
                    joFee_details = joPlanSubscription.getJSONArray("fee_details");
                    ArrayList<String> plans=new ArrayList<String>();
                    plans=Extras.getInstance().getPlansArray(joFee_details);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PlansActivity.this, android.R.layout.simple_spinner_item, plans);
                    ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sgrid.setAdapter(spinnerArrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                finish();
            }
        }
    }

}
